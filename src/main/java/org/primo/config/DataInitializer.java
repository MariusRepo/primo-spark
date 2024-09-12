package org.primo.config;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.primo.entities.GameSpin;
import org.primo.entities.Player;

import static org.primo.config.HibernateConfig.getSessionFactory;

// Demo purpose
public class DataInitializer {

    public static void addDBUsers() {
        try (Session session = getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            // Import default users
            Player user1 = new Player();
            user1.setPlayerName("Player1");
            user1.setWins(12);
            user1.setLosses(11);
            user1.setTotalSpins(23);

            Player user2 = new Player();
            user2.setPlayerName("Player2");
            user2.setLosses(0);
            user2.setTotalSpins(2);
            user2.setWins(2);

            session.save(user1);
            session.save(user2);
            session.save(new Player("DEFAULT"));

            // Import game spins
            GameSpin gameSpin = new GameSpin(user1, 3, true, "HHHH");
            GameSpin gameSpin2 = new GameSpin(user2, 5, true, "HHHH");

            session.save(gameSpin);
            session.save(gameSpin2);

            tx.commit();
        }
    }
}
