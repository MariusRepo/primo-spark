package org.primo.repositories;

import org.hibernate.Session;
import org.primo.entities.Player;

public interface PlayerRepository {
    Player findByPlayerName(String playerName);
    void save (Player player);
    void save (Player player, Session session);
}
