package org.primo.config;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.primo.entities.GameSpin;
import org.primo.entities.Player;

import java.time.LocalDateTime;

import static org.primo.utils.PrimoUtils.generateRandomNonce;
import static org.primo.utils.PrimoUtils.generateRandomSeed;
import static org.primo.utils.PrimoUtils.generateSecureNumber;
import static org.primo.utils.PrimoUtils.generateSpinToken;
import static org.primo.utils.PrimoUtils.isPrime;
import static org.primo.config.HibernateConfig.getSessionFactory;

// Demo purpose: Load data for testing purposes
public class DataInitializer {

    public static void addDBUsers() {
        try (Session session = getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            // Import default users
            Player user1 = new Player("Player1", 23, 11, 12);
            Player user2 = new Player("Player2", 2, 2, 0);
            session.save(user1);
            session.save(user2);
            session.save(new Player("DEFAULT"));

            // Import game spins
            String client = generateRandomSeed();
            String server = generateRandomSeed();
            int nunce = generateRandomNonce();
            int result = generateSecureNumber(client, server, nunce);
            boolean isPrime = isPrime(result);

            GameSpin gameSpin = new GameSpin(user1, client, server, nunce, result, isPrime, generateSpinToken(), LocalDateTime.now());

            String client2 = generateRandomSeed();
            String server2 = generateRandomSeed();
            int nunce2 = generateRandomNonce();
            int result2 = generateSecureNumber(client, server, nunce);
            boolean isPrime2 = isPrime(result2);
            GameSpin gameSpin2 = new GameSpin(user2, client2, server2, nunce2, result2, isPrime2, generateSpinToken(), LocalDateTime.now());

            String client3 = generateRandomSeed();
            String server3 = generateRandomSeed();
            int nunce3 = generateRandomNonce();
            int result3 = generateSecureNumber(client, server, nunce);
            boolean isPrime3 = isPrime(result2);
            GameSpin gameSpin3 = new GameSpin(user2, client3, server3, nunce3, result3, isPrime3, "1876AE415BCF03B", LocalDateTime.now());

            session.save(gameSpin);
            session.save(gameSpin2);
            session.save(gameSpin3);

            tx.commit();
        }
    }
}
