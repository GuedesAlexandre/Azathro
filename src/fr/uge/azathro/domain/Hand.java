package fr.uge.azathro.domain;

import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    public static Hand fromIndexes(Set<Integer> indexes, List<Card> availableCards) {
        var cards = indexes.stream()
                .sorted()
                .map(availableCards::get)
                .toList();
        return new Hand(cards);
    }
}
