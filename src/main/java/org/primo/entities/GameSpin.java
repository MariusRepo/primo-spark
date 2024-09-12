package org.primo.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "game_spins")
public class GameSpin {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(name = "reference_token", length = 64)
    private String referenceToken;

    @Column(name = "server_seed", length = 64, nullable = false)
    private String serverSeed;

    @Column(name = "client_seed", length = 64, nullable = false)
    private String clientSeed;

    @Column(name = "nonce", nullable = false)
    private int nonce;

    @Column(name = "win")
    private boolean win;

    @Column(name = "result", nullable = false)
    private int result;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public GameSpin(Player player, String serverSeed, String clientSeed, int nonce, int result, boolean win, String referenceToken, LocalDateTime createdAt) {
        this.player = player;
        this.serverSeed = serverSeed;
        this.clientSeed = clientSeed;
        this.nonce = nonce;
        this.result = result;
        this.win = win;
        this.referenceToken = referenceToken;
        this.createdAt = createdAt;
    }

}
