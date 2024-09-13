package org.primo.service;

import lombok.Getter;
import lombok.Setter;
import org.primo.utils.StatusEnum;

@Setter
@Getter
public class CachedSpin {
    private final String playerName;
    private final String spinToken;
    private String status;
    private Integer result;

    public CachedSpin(String playerName, String spinToken) {
        this.playerName = playerName;
        this.spinToken = spinToken;
        this.status = StatusEnum.PROCESSING.toString();
        this.result = null;
    }

}

