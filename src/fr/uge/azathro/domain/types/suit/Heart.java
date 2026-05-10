package fr.uge.azathro.domain.types.suit;

public record Heart() implements Suit {
    @Override
    public String toString() {
        return "♥";
    }
}
