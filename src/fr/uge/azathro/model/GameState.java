package fr.uge.azathro.model;

import fr.uge.azathro.domain.Blind;
import fr.uge.azathro.domain.Card;
import fr.uge.azathro.domain.Deck;

import java.util.Objects;
import java.util.List;
import java.util.ArrayList;

public class GameState {
	private static final int NB_BLINDS = 5;

	private Blind currentBlind;
	private final Deck deck;
	private PlayerState playerState;
	private ArrayList<Card> currentHand;
	private int blindCount;


	public GameState(Blind currentBlind, Deck deck, PlayerState playerState) {
		Objects.requireNonNull(currentBlind);
		Objects.requireNonNull(deck);
		Objects.requireNonNull(playerState);
		this.currentBlind = currentBlind;
		this.deck = deck;
		this.playerState = playerState;
		this.currentHand = new ArrayList<>();
		this.blindCount = 1;
	}
	
	public Blind currentBlind() {
		return currentBlind;
	}

	public Deck deck() {
		return deck;
	}
	
	public PlayerState playerState() {
        return playerState;
    }

	public List<Card> currentHand() {
        return currentHand;
    }

    public void setCurrentHand(List<Card> cards) {
        Objects.requireNonNull(cards);
		currentHand = new ArrayList<>(cards);
    }

    public void updatePlayerState(PlayerState playerState) {
		Objects.requireNonNull(playerState);
        this.playerState = playerState;
    }

	public void updateCurrentBlind(Blind blind){
		Objects.requireNonNull(blind);
		this.currentBlind = blind;
	}

	
	public void increaseBlindCount() {
		blindCount++;
	}

	public boolean isFinished() {
		return blindCount > NB_BLINDS;
	}


	@Override
	public String toString() {
		return """
				════════════════════════════════════
					Blind n°%d
				    	Blind: %s
				    	Objectif: %.2f
				════════════════════════════════════
				       \s""".formatted(blindCount, currentBlind.name(), currentBlind.score());

	}

	public void removeCardsFromHand(List<Card> cardsToRemove) {
		Objects.requireNonNull(cardsToRemove);
        this.currentHand = currentHand.stream()
                .filter(c -> !cardsToRemove.contains(c))
				.collect(ArrayList::new	, ArrayList::add, ArrayList::addAll);
	}

	public void fillHandToSize(int targetSize) {
		if(targetSize <0 || targetSize > 8) {
			throw new IllegalArgumentException();
		}
		var currentHand = new ArrayList<>(this.currentHand);
		var cardsToDraw = targetSize - currentHand.size();
		currentHand.addAll(deck.draw(cardsToDraw));
		this.currentHand = currentHand;
	}

	public void discardCards(List<Card> cards) {
		Objects.requireNonNull(cards);
		deck.addToDiscard(cards);
	}

}

