package fr.uge.azathro.domain;

import java.util.List;
import java.util.Objects;

public class Hand {
    private final List<Card> cards;

    public Hand(List<Card> cards) {
        Objects.requireNonNull(cards);
        this.cards = cards;
    }
    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("Votre main : ").append(System.lineSeparator());
        cards.forEach(card -> sb.append(card.toString()).append(System.lineSeparator()));
        return sb.toString();
    }

    public List<Card> cards() {
        return cards;
    }
}
