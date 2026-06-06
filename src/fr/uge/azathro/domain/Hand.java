package fr.uge.azathro.domain;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public record Hand(List<Card> cards) {
    public Hand {
        Objects.requireNonNull(cards);
    }
    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("Votre main : ").append(System.lineSeparator());
        cards.forEach(card -> sb.append(card.toString()).append(System.lineSeparator()));
        return sb.toString();
    }

    public static Hand fromIndexes(Set<Integer> indexes, List<Card> availableCards) {
       Objects.requireNonNull(indexes);
       Objects.requireNonNull(availableCards);
        var cards = indexes.stream()
                .sorted()
                .map(availableCards::get)
                .toList();
        return new Hand(cards);
    }
}
