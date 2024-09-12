package org.primo.service;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.primo.entities.GameSpin;
import org.primo.entities.Play;
import org.primo.entities.Player;
import org.primo.exceptions.PlayerException;
import org.primo.repositories.GameSpinRepository;
import org.primo.repositories.PlayerRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.primo.PrimoUtils.createUUIDReference;
import static org.primo.PrimoUtils.generateRandomNonce;
import static org.primo.PrimoUtils.generateRandomNumber;
import static org.primo.PrimoUtils.generateRandomSeed;
import static org.primo.PrimoUtils.isPrime;
import static org.primo.config.HibernateConfig.getSessionFactory;

public class PrimoService {

    public static final String WIN = "WIN";
    public static final String LOSS = "LOSS";
    private final GameSpinRepository gameSpinRepository;
    private final PlayerRepository playerRepository;

    public PrimoService(GameSpinRepository gameSpinRepository, PlayerRepository playerRepository) {
        this.gameSpinRepository = gameSpinRepository;
        this.playerRepository = playerRepository;
    }

    public Play spin(String playerName) {
        Player player = playerRepository.findByPlayerName(playerName);
        if (player == null) {
            throw new PlayerException("Player is missing!");
        }

        // Game play
        String serverSeed = generateRandomSeed();
        String clientSeed = generateRandomSeed(); // this is
        int nonce = generateRandomNonce();
        int result = generateRandomNumber(serverSeed, clientSeed, nonce);
        boolean isPrime = isPrime(result);

        // DB transactions atomic and asynchronous
        asyncDBUpdate(player, serverSeed, clientSeed, nonce, result, isPrime);

        return new Play(playerName, createUUIDReference(UUID.randomUUID()), result, isPrime ? WIN : LOSS);
    }

    private void asyncDBUpdate(Player player, String serverSeed, String clientSeed, int nonce, int result, boolean isPrime){
        updatePlayerStats(player, isPrime);
        AsyncProcessor.submitTask(() -> {
            try {
                saveSpin(player, serverSeed, clientSeed, nonce, result, isPrime);
            } catch (Exception e) {
                e.printStackTrace(); // TODO - Error handling
            }
        });
    }

    private void saveSpin(Player player, String serverSeed, String clientSeed, int nonce, int result, boolean isPrime) {
        try (Session session = getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                updatePlayerStats(player, isPrime);
                gameSpinRepository.recordSpin(player, serverSeed, clientSeed, nonce, result, isPrime, session);
                playerRepository.save(player, session);
                tx.commit();
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                throw e; // TODO - Error handling
            }
        }
    }

    public List<Play> results() {
        return spinToPlay(gameSpinRepository.getAllSpins());
    }

    public List<Play> result(String playerName) {
        return spinToPlay(gameSpinRepository.findByUserName(playerName));
    }

    public List<Play> spinToPlay(@NotNull List<GameSpin> gameSpins) {
        return gameSpins.stream()
                .map(s -> new Play(s.getPlayer().getPlayerName(), s.getReference(), s.getNumber(), s.isWin() ? WIN : LOSS))
                .collect(Collectors.toList());
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
