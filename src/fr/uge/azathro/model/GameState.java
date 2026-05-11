package fr.uge.azathro.model;

import fr.uge.azathro.domain.Blind;
import fr.uge.azathro.domain.Deck;

import java.util.Objects;

public class GameState {
	private final Blind currentBlind;
	private final Deck deck;
	private int blindCount;
	private int totalScore;
	private int handsRemaining;

	public GameState(Blind currentBlind, Deck deck) {
		Objects.requireNonNull(currentBlind);
		Objects.requireNonNull(deck);

		this.currentBlind = currentBlind;
		this.deck = deck;
		this.blindCount = 1;
		this.totalScore = 0;
		this.handsRemaining = 4;
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

	public int totalScore() {
		return totalScore;
	}

	public int handsRemaining() {
		return handsRemaining;
	}

	public void addScore(int score) {
		totalScore += score;
	}

	public void decreaseHandsRemaining() {
		handsRemaining--;
	}

	public boolean blindCleared() {
		return totalScore >= currentBlind.score();
	}

	public boolean isGameOver() {
		return handsRemaining <= 0;
	}

	public void increaseBlindCount() {
		blindCount++;
	}

	public boolean isFinish() {
		return blindCount == 5;
	}

	@Override
	public String toString() {
		return """
				════════════════════════════════════
					Blind n°%d
				    	Blind: %s
				    	Objectif: %d
				    	Score total: %d
				    	Mains restantes: %d
				════════════════════════════════════
				        """.formatted(blindCount, currentBlind.name(), currentBlind.score(), totalScore,
				handsRemaining);

	}
}
