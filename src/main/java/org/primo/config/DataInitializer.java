package org.primo.config;

import org.primo.entities.Player;
import org.primo.repositories.GameSpinRepository;
import org.primo.repositories.PlayerRepository;

import static org.primo.utils.PrimoUtils.generateRandomNonce;
import static org.primo.utils.PrimoUtils.generateRandomSeed;
import static org.primo.utils.PrimoUtils.generateSecureNumber;
import static org.primo.utils.PrimoUtils.generateSpinToken;
import static org.primo.utils.PrimoUtils.isPrime;

// Demo purpose: Load data for testing purposes
public class DataInitializer {

    public static void insertDBData(GameSpinRepository gameSpinRepository, PlayerRepository playerRepository) {
        for (int i = 0; i < 5; i++) {
            Player player = new Player("Player" + i);
            for (int j = 0; j < i; j++) {
                String client = generateRandomSeed();
                String server = generateRandomSeed();
                int nunce = generateRandomNonce();
                int result = generateSecureNumber(client, server, nunce);
                boolean isPrime = isPrime(result);
                player.incrementSpins();
                if (isPrime) {
                    player.incrementWins();
                } else {
                    player.incrementLosses();
                }
                gameSpinRepository.recordSpin(player, server, client, nunce, generateSpinToken());
                playerRepository.saveOrUpdatePlayer(player);
            }
        }
    }
}
