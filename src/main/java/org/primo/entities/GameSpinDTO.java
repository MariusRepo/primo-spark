package org.primo.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.primo.utils.StatusEnum;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GameSpinDTO {
    private String playerName;
    private String spinReference;
    private Integer number;
    private String result;
}
