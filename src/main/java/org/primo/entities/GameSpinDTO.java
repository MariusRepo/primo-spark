package org.primo.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GameSpinDTO {
    private String playerName;
    private String spinReference;
    private int number;
    private String result;
}
