package main.java.fr.uge.azathro.domain.types.suit;

public record Diamond() implements Suit {

    @Override
    public String toString() {
        return "♦";
    }
}
