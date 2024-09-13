package org.primo.utils;

public enum ResultEnum {
    WIN("WIN"),
    LOSS("LOSS");

    private final String message;

    ResultEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
