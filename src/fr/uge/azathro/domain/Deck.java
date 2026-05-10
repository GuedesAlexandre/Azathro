package fr.uge.azathro.domain;

import fr.uge.azathro.domain.types.rank.*;
import fr.uge.azathro.domain.types.suit.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class Deck {
    private static final ArrayList<Card> decks = new ArrayList<>();

    public Deck(){
        createStandardDeck();
    }

    public void addCard(Card card) {
        Objects.requireNonNull(card);
        decks.add(card);
    }

    public int size(){
        return decks.size();
    }

    public void clear(){
        if(size()<5){
            decks.clear();
            createStandardDeck();
        }
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

    public List<Card> draw(int size) {
        if(size>8){throw new IllegalArgumentException("size can't be greater than 8");}
        int toDraw = Math.min(size, decks.size());
        return IntStream.range(0, toDraw)
                .mapToObj(_ -> decks.removeLast())
                .toList();
    }

}
