package main.java.fr.uge.azathro.domain.types.suit;

public record Spade() implements Suit {

    @Override
    public String toString() {
        return "♠";
    }
}
