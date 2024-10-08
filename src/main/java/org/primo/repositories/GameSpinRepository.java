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

    public void recordSpin(Player player, String serverSeed, String clientSeed, int nonce, String spinToken) {
        execute(session -> {
            GameSpin gameSpin = new GameSpin(player, serverSeed, clientSeed, nonce, spinToken, LocalDateTime.now());
            session.saveOrUpdate(player);
            session.save(gameSpin);
            return null;
        });
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
