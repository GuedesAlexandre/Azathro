package fr.uge.azathro.domain;

import fr.uge.azathro.domain.types.rank.Rank;
import fr.uge.azathro.domain.types.suit.Suit;

import java.util.Objects;

public record Card(Suit suit, Rank rank) {
    public Card {
        Objects.requireNonNull(suit);
        Objects.requireNonNull(rank);
    }

    @Override
    public String toString() {
        return suit + " " + rank.toString() + "(" + rank.value() + ")";
    }


}
