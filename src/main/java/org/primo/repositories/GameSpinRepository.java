package org.primo.repositories;

import org.hibernate.SessionFactory;
import org.primo.entities.GameSpin;
import org.primo.entities.Player;

import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class GameSpinRepository extends BaseRepository<GameSpin> {
    public GameSpinRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<GameSpin> findByUserName(String playerName) {
        return execute(session -> {
            TypedQuery<GameSpin> query = session.createQuery(
                    "SELECT g FROM GameSpin g JOIN FETCH g.player p WHERE p.playerName = :playerName",
                    GameSpin.class
            );
            query.setParameter("playerName", playerName);
            return findListResult(query);
        });
    }

    public List<GameSpin> getAllSpins() {
        return execute(session -> {
            TypedQuery<GameSpin> query = session.createQuery("SELECT g FROM GameSpin g JOIN FETCH g.player", GameSpin.class);
            return findListResult(query);
        });
    }

    public void recordSpin(Player player, String serverSeed, String clientSeed, int nonce, int result, boolean isPrime, String spinToken) {
        execute(session -> {
            pause(20); // Simulate a delay
            GameSpin gameSpin = new GameSpin(player, serverSeed, clientSeed, nonce, result, isPrime, spinToken, LocalDateTime.now());
            session.save(gameSpin);
            session.merge(player);
            return null;
        });
    }

    private void pause(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Optional<GameSpin> findSpinByTokenAndPlayerName(String spinToken, String playerName) {
        return execute(session -> {
            TypedQuery<GameSpin> query = session.createQuery(
                    "SELECT gs FROM GameSpin gs JOIN FETCH gs.player p WHERE gs.referenceToken = :spinToken AND p.playerName = :playerName",
                    GameSpin.class
            );
            query.setParameter("spinToken", spinToken);
            query.setParameter("playerName", playerName);
            return findSingleResult(query);
        });
    }

    public int getNextNonce(String clientSeed) {
        return execute(session -> {
            String hql = "SELECT COALESCE(MAX(gs.nonce), 0) + 1 FROM GameSpin gs WHERE gs.clientSeed = :clientSeed";
            Integer nextNonce = (Integer) session.createQuery(hql)
                    .setParameter("clientSeed", clientSeed)
                    .uniqueResult();
            return (nextNonce != null) ? nextNonce : 1;
        });
    }

}
