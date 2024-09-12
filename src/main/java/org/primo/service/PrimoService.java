package org.primo.service;

import org.jetbrains.annotations.NotNull;
import org.primo.entities.GameSpin;
import org.primo.entities.Play;
import org.primo.entities.Player;
import org.primo.exceptions.PlayerException;
import org.primo.repositories.PlayerRepository;
import org.primo.repositories.GameSpinRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.primo.PrimoUtils.generateRandomNonce;
import static org.primo.PrimoUtils.generateRandomSeed;
import static org.primo.PrimoUtils.generateSecureNumber;
import static org.primo.PrimoUtils.generateSpinToken;
import static org.primo.PrimoUtils.isPrime;

public class PrimoService {

    public static final String WIN = "WIN";
    public static final String LOSS = "LOSS";
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

        int result = generateSecureNumber(serverSeed, clientSeed, nonce);

        // Update DB asynchronously and atomic
        return recordSpinDetails(player, serverSeed, clientSeed, nonce, result);
    }

    public List<Play> allSpins() {
        return spinsToPlays(gameSpinRepository.getAllSpins());
    }

    public List<Play> playerSpins(String playerName) {
        return spinsToPlays(gameSpinRepository.findByUserName(playerName));
    }

    public Play spinToPlay(GameSpin gameSpins) {
        return new Play(gameSpins.getPlayer().getPlayerName(), gameSpins.getReferenceToken(), gameSpins.getResult(), gameSpins.isWin() ? WIN : LOSS);
    }

    public List<Play> spinsToPlays(@NotNull List<GameSpin> gameSpins) {
        return gameSpins.stream()
                .map(this::spinToPlay)
                .collect(Collectors.toList());
    }

    public Play checkSpinStatus(String playerName, String spinToken) {
        return gameSpinRepository.findSpinByTokenAndPlayerName(spinToken, playerName)
                .map(this::spinToPlay)
                .orElse(null); // TODO - Not ready or missing
    }

    private String recordSpinDetails(Player player, String serverSeed, String clientSeed, int nonce, int result) {
        String spinToken = generateSpinToken();
        CompletableFuture.runAsync(() -> processGameSpin(player, serverSeed, clientSeed, nonce, result, spinToken), executorService);
        return spinToken;
    }

    private void processGameSpin(Player player, String serverSeed, String clientSeed, int nonce, int result, String spinToken) {
        boolean isPrime = isPrime(result);
        updatePlayerStats(player, isPrime);

        AsyncProcessor.submitTask(() -> {
            pause(10);
            try {
                gameSpinRepository.recordSpin(player, serverSeed, clientSeed, nonce, result, isPrime, spinToken);
            } catch (Exception e) {
                e.printStackTrace(); // TODO - Error handling
            }
        });
    }

    // Simulate a delay
    private void pause(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
