package fr.uge.azathro.domain.types.suit;

public record Club() implements Suit {
    @Override
    public String toString() {
        return "♣";
    }
}
