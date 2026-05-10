package main.java.fr.uge.azathro.domain;

import main.java.fr.uge.azathro.domain.types.rank.*;
import main.java.fr.uge.azathro.domain.types.suit.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class Deck {
    private static final ArrayList<Card> decks = new ArrayList<>();

    public Deck(){
        createStandardDeck();
    }

    private void addCard(Card card) {
        decks.add(card);
    }

    public static void createStandardDeck() {
        List<Suit> suits = List.of(new Club(), new Diamond(), new Heart(), new Spade());
        for (Suit suit : suits) {
            decks.add(new Card(suit, new Ace()));
            decks.add(new Card(suit, new King()));
            decks.add(new Card(suit, new Queen()));
            decks.add(new Card(suit, new Jack()));

            for (int value = 2; value <= 10; value++) {
                decks.add(new Card(suit, new NumericRank(value)));
            }
        }

        Collections.shuffle(decks);
    }

    public List<Card> draw(List<Card> deck, int size) {
        int toDraw = Math.min(size, deck.size());
        return IntStream.range(0, toDraw)
                .mapToObj(i -> deck.removeLast())
                .toList();
    }

}
