package org.primo.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "player")
@Getter
@Setter
@NoArgsConstructor
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "name", nullable = false)
    private String playerName;
    private int totalSpins;
    private int wins;
    private int losses;

    public Player(String playerName) {
        this.playerName = playerName;
        this.totalSpins = 0;
        this.wins = 0;
        this.losses = 0;
    }

    public Player(String playerName, int totalSpins, int wins, int losses) {
        this.playerName = playerName;
        this.totalSpins = totalSpins;
        this.wins = wins;
        this.losses = losses;
    }

    public void incrementSpins() {
        this.totalSpins++;
    }

    public void incrementWins() {
        this.wins++;
    }

    public void incrementLosses() {
        this.losses++;
    }
}
