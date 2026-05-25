package fr.uge.azathro.domain;

import fr.uge.azathro.domain.types.rank.*;
import fr.uge.azathro.domain.types.suit.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class Deck {
	private final ArrayList<Card> deck = new ArrayList<>();
	private final ArrayList<Card> discardPile = new ArrayList<>();

	public Deck() {
		createStandardDeck();
	}

	public int size() {
		return deck.size();
	}

	public void createStandardDeck() {
		deck.clear();
		discardPile.clear();
		for (Suit suit : Suit.values()) {
			for (Rank rank : Rank.values()) {
				deck.add(new Card(suit, rank));
			}
		}
		Collections.shuffle(deck);
	}

	public List<Card> draw(int size) {
		if (size < 0 || size > 8) {
			throw new IllegalArgumentException("size must be between 0 and 8");
		}
		refillDeck(size);
		int toDraw = Math.min(size, deck.size());
		return IntStream.range(0, toDraw).mapToObj(_ -> deck.removeLast()).toList();
	}

	public void addToDiscard(List<Card> cards) {
		Objects.requireNonNull(cards);
		discardPile.addAll(cards);
	}

	public void refillDeck(int cardsToDraw) {
		if (cardsToDraw < 0 || cardsToDraw > 8) {
			throw new IllegalArgumentException("cards to draw must be between 0 and 8");
		}
		if (deck.size() >= cardsToDraw) {
			return;
		}
		deck.addAll(discardPile);
		discardPile.clear();
		Collections.shuffle(deck);
	}

}
