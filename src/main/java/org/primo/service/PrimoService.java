package org.primo.service;

import org.jetbrains.annotations.NotNull;
import org.primo.entities.GameSpin;
import org.primo.entities.GameSpinDTO;
import org.primo.entities.Player;
import org.primo.entities.PlayerDTO;
import org.primo.exceptions.ParameterException;
import org.primo.exceptions.PlayerException;
import org.primo.exceptions.SpinException;
import org.primo.repositories.GameSpinRepository;
import org.primo.repositories.PlayerRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.primo.service.SpinCache.getCache;
import static org.primo.utils.PrimoUtils.generateRandomNonce;
import static org.primo.utils.PrimoUtils.generateRandomSeed;
import static org.primo.utils.PrimoUtils.generateSecureNumber;
import static org.primo.utils.PrimoUtils.generateSpinToken;
import static org.primo.utils.PrimoUtils.isPrime;
import static org.primo.utils.ResultEnum.LOSS;
import static org.primo.utils.ResultEnum.WIN;

public class PrimoService {

    private final GameSpinRepository gameSpinRepository;
    private final PlayerRepository playerRepository;
    private final ExecutorService executorService;

    public PrimoService(GameSpinRepository gameSpinRepository, PlayerRepository playerRepository) {
        this.gameSpinRepository = gameSpinRepository;
        this.playerRepository = playerRepository;
        this.executorService = Executors.newFixedThreadPool(11);
    }

    public String spin(String playerName, String clientSeed) {
        Player player = Optional.ofNullable(playerRepository.findByPlayerName(playerName))
                .get()
                .orElseThrow(() -> new PlayerException("Player is missing!"));

        // For simplicity, I will generate this number randomly
        // I would recommend to have it incremental with DB
        int nonce = generateRandomNonce();
        String serverSeed = generateRandomSeed();

        // Update DB asynchronously and atomic
        return recordSpinDetails(player, serverSeed, clientSeed, nonce);
    }

    public List<GameSpinDTO> allSpins() {
        return spinToDTO(gameSpinRepository.getAllSpins());
    }

    public List<GameSpinDTO> playerSpins(String playerName) {
        return spinToDTO(gameSpinRepository.findByUserName(playerName));
    }

    public GameSpinDTO spinToDTO(GameSpin gameSpins) {
        int result = generateSecureNumber(gameSpins.getClientSeed(), gameSpins.getClientSeed(), gameSpins.getNonce());
        return new GameSpinDTO(gameSpins.getPlayer().getPlayerName(), gameSpins.getReferenceToken(), result, isPrime(result) ? WIN.getMessage() : LOSS.getMessage());
    }

    public List<GameSpinDTO> spinToDTO(@NotNull List<GameSpin> gameSpins) {
        return gameSpins.stream()
                .map(this::spinToDTO)
                .collect(Collectors.toList());
    }

    public PlayerDTO playerToDTO(Player player) {
        return new PlayerDTO(player.getPlayerName(), player.getTotalSpins(), player.getWins(), player.getLosses());
    }

    public List<PlayerDTO> playerToDTO(@NotNull List<Player> players) {
        return players.stream()
                .map(this::playerToDTO)
                .collect(Collectors.toList());
    }

    public GameSpinDTO checkSpinStatus(String playerName, String spinToken) {
        CachedSpin cachedSpin = getCache().get(spinToken);
        if (cachedSpin == null) {
            GameSpinDTO gameSpin = checkDBStatus(playerName, spinToken);
            if (gameSpin != null) {
                cachedSpin = new CachedSpin(gameSpin.getPlayerName(), spinToken);
                cachedSpin.setStatus(gameSpin.getResult());
                cachedSpin.setResult(gameSpin.getNumber());
                getCache().put(spinToken, cachedSpin);
            } else {
                throw new SpinException("Spin is missing or processing ...");
            }
        }
        return cacheSpinToDTO(cachedSpin);
    }

    private GameSpinDTO cacheSpinToDTO(CachedSpin cachedSpin) {
        return new GameSpinDTO(cachedSpin.getPlayerName(), cachedSpin.getSpinToken(), cachedSpin.getResult(), cachedSpin.getStatus());
    }

    public GameSpinDTO checkDBStatus(String playerName, String spinToken) {
        return gameSpinRepository.findSpinByTokenAndPlayerName(spinToken, playerName)
                .map(this::spinToDTO)
                .orElse(null);
    }

    public PlayerDTO playerDetails(String playerName) {
        return playerRepository.findByPlayerName(playerName)
                .map(this::playerToDTO)
                .orElseThrow(() -> new PlayerException("Player is missing!"));
    }

    public List<PlayerDTO> playersDetails() {
        return playerRepository.getAllPlayers().stream()
                .map(this::playerToDTO)
                .collect(Collectors.toList());
    }

    public String validateParam(String paramValue, String errorMessage) {
        return Optional.ofNullable(paramValue)
                .filter(s -> !s.trim().isEmpty())
                .orElseThrow(() -> new ParameterException(errorMessage));
    }

    private String recordSpinDetails(Player player, String serverSeed, String clientSeed, int nonce) {
        String spinToken = generateSpinToken();
        CompletableFuture.runAsync(() -> processGameSpin(player, serverSeed, clientSeed, nonce, spinToken), executorService);
        return spinToken;
    }

    private CompletableFuture<Void> processGameSpin(Player player, String serverSeed, String clientSeed, int nonce, String spinToken) {
        CachedSpin cachedSpin = new CachedSpin(player.getPlayerName(), spinToken);
        getCache().put(spinToken, cachedSpin);
        return CompletableFuture.runAsync(() -> {
            pause(); // Simulate load
            try {
                int result = generateSecureNumber(serverSeed, clientSeed, nonce);
                boolean isPrime = isPrime(result);
                updatePlayerStats(player, isPrime);
                gameSpinRepository.recordSpin(player, serverSeed, clientSeed, nonce, spinToken);
                cachedSpin.setStatus(isPrime ? WIN.toString() : LOSS.toString());
                cachedSpin.setResult(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void pause() {
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void updatePlayerStats(@NotNull Player player, boolean isPrime) {
        player.incrementSpins();
        if (isPrime) {
            player.incrementWins();
        } else {
            player.incrementLosses();
        }
    }
}
