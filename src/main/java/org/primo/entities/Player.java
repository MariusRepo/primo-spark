package org.primo.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.util.UUID;

import static org.primo.PrimoUtils.createUUIDReference;

@Entity
@Table(name = "player")
@Getter
@Setter
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    private String playerName;
    private String reference;
    private int totalSpins;
    private int wins;
    private int losses;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (reference == null) {
            reference = createUUIDReference(id);
        }
    }

    public Player() {
    }

    public Player(String playerName) {
        this.playerName = playerName;
        this.totalSpins = 0;
        this.wins = 0;
        this.losses = 0;
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
