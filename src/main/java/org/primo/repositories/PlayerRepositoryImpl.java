package org.primo.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.primo.entities.Player;

import javax.persistence.TypedQuery;

public class PlayerRepositoryImpl implements PlayerRepository {
    private final SessionFactory sessionFactory;

    public PlayerRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Player findByPlayerName(String playerName) {
        try (Session session = sessionFactory.openSession()) {
            TypedQuery<Player> query = session.createQuery("SELECT u FROM Player u WHERE u.playerName = :playerName", Player.class);
            query.setParameter("playerName", playerName);
            var playerList = query.getResultList();

            return playerList.isEmpty() ? null : playerList.get(0); // TODO - Deal with multiple results
        }
    }

    public void save(Player player) {
        try (Session session = sessionFactory.openSession()) {
            session.getTransaction().begin();
            save(player, session);
            session.getTransaction().commit();
        }
    }

    public void save(Player player, Session session) {
        if (player.getId() == null) {
            session.persist(player);
        } else {
            session.merge(player);
        }
    }
}
