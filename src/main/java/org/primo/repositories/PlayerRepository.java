package org.primo.repositories;

import org.hibernate.SessionFactory;
import org.primo.entities.Player;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class PlayerRepository extends BaseRepository<Player> {
    public PlayerRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<Player> findByPlayerName(String playerName) {
        return execute(session -> {
            TypedQuery<Player> query = session.createQuery(
                    "SELECT u FROM Player u WHERE u.playerName = :playerName", Player.class
            );
            query.setParameter("playerName", playerName);
            return findSingleResult(query);
        });
    }

    public List<Player> getAllPlayers() {
        return execute(session -> {
            TypedQuery<Player> query = session.createQuery("SELECT p FROM Player p", Player.class);
            return findListResult(query);
        });
    }

}
