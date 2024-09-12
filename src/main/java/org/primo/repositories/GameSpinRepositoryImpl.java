package org.primo.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.primo.entities.GameSpin;
import org.primo.entities.Player;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

public class GameSpinRepositoryImpl implements GameSpinRepository {
    private final SessionFactory sessionFactory;

    public GameSpinRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<GameSpin> findByUserName(String playerName) {
        List<GameSpin> gameSpins;
        try (Session session = sessionFactory.openSession()) {
            session.getTransaction().begin();
            TypedQuery<GameSpin> query = session.createQuery(
                    "SELECT g FROM GameSpin g JOIN FETCH g.player p WHERE p.playerName = :playerName",
                    GameSpin.class
            );
            query.setParameter("playerName", playerName);
            gameSpins = query.getResultList();
            session.getTransaction().commit();
        }
        return gameSpins;
    }

    public List<GameSpin> getAllSpins() {
        List<GameSpin> gameSpins = null;
        try (Session session = sessionFactory.openSession()) {
            session.getTransaction().begin();
            TypedQuery<GameSpin> query = session.createQuery(
                    "SELECT g FROM GameSpin g JOIN FETCH g.player",
                    GameSpin.class
            );
            gameSpins = query.getResultList();
            session.getTransaction().commit();
        }
        return gameSpins;
    }

    public UUID recordSpin(Player player, String serverSeed, String clientSeed, int nonce, int result, boolean isPrime) {
        UUID gameSpinID;
        try (Session session = sessionFactory.openSession()) {
            gameSpinID = save(new GameSpin(player, result, isPrime, serverSeed + clientSeed + nonce), session);
        }
        return gameSpinID;
    }

    public UUID recordSpin(Player player, String serverSeed, String clientSeed, int nonce, int result, boolean isPrime, Session session) {
        return save(new GameSpin(player, result, isPrime, serverSeed + clientSeed + nonce), session);
    }

    private UUID save(GameSpin gameSpin, Session session) {
        session.persist(gameSpin);
        return gameSpin.getId();
    }
}
