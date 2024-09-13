package org.primo.utils;

public enum StatusEnum {
    SUCCESS("SUCCESS"),
    ERROR("ERROR"),
    PROCESSING("PROCESSING"),
    FAILURE("FAILURE");

    private final String message;

    StatusEnum(String message) {
        this.message = message;
    }

    public String str() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}
