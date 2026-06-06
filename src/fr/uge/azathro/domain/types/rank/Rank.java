package fr.uge.azathro.domain.types.rank;

import java.util.Objects;

public enum Rank {
    TWO(2, "2"),
    THREE(3, "3"),
    FOUR(4, "4"),
    FIVE(5, "5"),
    SIX(6, "6"),
    SEVEN(7, "7"),
    EIGHT(8, "8"),
    NINE(9, "9"),
    TEN(10, "10"),
    JACK(11, "Jack"),
    QUEEN(12, "Queen"),
    KING(13, "King"),
    ACE(14, "Ace");

    private final int value;
    private final String name;

    Rank(int value, String name) {
        if(value < 1 || value > 14){
            throw new IllegalArgumentException();
        }
        Objects.requireNonNull(name);
        this.value = value;
        this.name = name;
    }

    public int value() {
        return value;
    }

    @Override
    public String toString() {
        return name;
    }
}