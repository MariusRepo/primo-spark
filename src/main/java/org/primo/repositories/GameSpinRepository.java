package org.primo.repositories;

import org.hibernate.Session;
import org.primo.entities.GameSpin;
import org.primo.entities.Player;

import java.util.List;
import java.util.UUID;

public interface GameSpinRepository {
    List<GameSpin> findByUserName(String playerName);
    List<GameSpin> getAllSpins();
    UUID recordSpin(Player player, String serverSeed, String clientSeed, int nonce, int result, boolean isPrime);
    UUID recordSpin(Player player, String serverSeed, String clientSeed, int nonce, int result, boolean isPrime, Session session);
}
