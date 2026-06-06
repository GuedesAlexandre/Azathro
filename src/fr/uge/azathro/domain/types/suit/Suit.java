package fr.uge.azathro.domain.types.suit;

import java.util.Objects;

public enum Suit {
    CLUB("♣"),
    DIAMOND("♦"),
    HEART("♥"),
    SPADE("♠");

    private final String symbol;

    Suit(String symbol) {
        Objects.requireNonNull(symbol);
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}