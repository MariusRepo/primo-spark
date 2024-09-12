package org.primo.entities;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.util.UUID;

import static org.primo.PrimoUtils.createUUIDReference;

@Entity
@Table(name = "game_spin")
@Getter
public class GameSpin {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playerId")
    private Player player;
    private int number;
    private boolean win;
    private String reference;
    private String hashCode;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (reference == null){
            reference = createUUIDReference(id);
        }
    }

    public GameSpin() {
    }

    public GameSpin(Player player, int number, boolean win, String hashCode) {
        this.player = player;
        this.number = number;
        this.win = win;
        this.hashCode = hashCode;
    }

    @Override
    public String toString() {
        return "GameSpin{" +
                "id=" + id +
                ", number=" + number +
                ", win=" + win +
                ", hashCode='" + hashCode + '\'' +
                '}';
    }
}
