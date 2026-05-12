package fr.uge.azathro.model;

import fr.uge.azathro.domain.Blind;
import fr.uge.azathro.domain.Deck;

import java.util.Objects;

public class GameState {
	private final Blind currentBlind;
	private final Deck deck;
	private int blindCount;


	public GameState(Blind currentBlind, Deck deck) {
		Objects.requireNonNull(currentBlind);
		Objects.requireNonNull(deck);
		this.currentBlind = currentBlind;
		this.deck = deck;
		this.blindCount = 1;
	}
	
	public Blind currentBlind() {
		return currentBlind;
	}

	public Deck deck() {
		return deck;
	}

	public int blindCount() {
		return blindCount;
	}

	
	
	public void increaseBlindCount() {
		blindCount++;
	}

	public boolean isFinished() {
		return blindCount > 5; // 5 blinds / game
	}

	@Override
	public String toString() {
		return """
				════════════════════════════════════
					Blind n°%d
				    	Blind: %s
				    	Objectif: %d
				════════════════════════════════════
				        """.formatted(blindCount, currentBlind.name(), currentBlind.score());

	}
}
