package org.primo.utils;

public enum StatusEnum {
    SUCCESS("SUCCESS"),
    COMPLETED("COMPLETED"),
    PROCESSING("PROCESSING"),
    FAILURE("FAILURE");

    private final String message;

    StatusEnum(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
