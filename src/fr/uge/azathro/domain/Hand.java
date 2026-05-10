package fr.uge.azathro.domain;

import java.util.List;

public record Hand(List<Card> cards) {
    public Hand{
        if(cards.isEmpty()){
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("Votre main : ").append(System.lineSeparator());
        cards.forEach(card -> sb.append(card.toString()).append(System.lineSeparator()));
        return sb.toString();
    }
}
