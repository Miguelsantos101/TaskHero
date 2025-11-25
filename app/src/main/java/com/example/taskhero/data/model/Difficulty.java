package com.example.taskhero.data.model;

public enum Difficulty {
    EASY(0),
    MEDIUM(1),
    HARD(2);

    private final int value;

    Difficulty(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Difficulty fromInt(int value) {
        for (Difficulty difficulty : Difficulty.values()) {
            if (difficulty.getValue() == value) {
                return difficulty;
            }
        }
        return MEDIUM;
    }
}