package fr.uge.azathro.controller;

import fr.uge.azathro.model.GameState;
import fr.uge.azathro.model.PlayerState;
import fr.uge.azathro.domain.Card;
import fr.uge.azathro.domain.types.combination.*;
import fr.uge.azathro.domain.Hand;
import fr.uge.azathro.domain.HandEvaluator;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class GameController {
	private static final int HANDSIZE = 8;
	private final GameState gameState;
	private final Scanner scanner = new Scanner(System.in);

	public GameController(GameState gameState) {
		this.gameState = gameState;
	}

	public void startGame() {
		while (!gameState.isFinished()) {
			if (!playBlind()) {
				return;
			}

			gameState.increaseBlindCount();
			var oldPlayer = gameState.playerState();
			var newPlayer = new PlayerState(oldPlayer.name(), 0, PlayerState.HANDCOUNT);
			gameState.updatePlayerState(newPlayer);
			gameState.deck().createStandardDeck();
			gameState.setCurrentHand(List.of());
		}
		IO.println("""
								\s
				╔══════════════════════════════╗
				     ⚝  Partie terminée !  ⚝
				╚══════════════════════════════╝
								  \s""");
	}

	private boolean playBlind() {
		IO.println(gameState);
		IO.println(gameState.playerState());
		while (!gameState.currentBlind().isBlinded(gameState.playerState())
				&& gameState.playerState().handCount() > 0) {
			playHand();
		}

		if (gameState.currentBlind().isBlinded(gameState.playerState())) {
			IO.println("""
									\s
					╔══════════════════════════════╗
					      ⚝  Blind réussi !  ⚝
					╚══════════════════════════════╝
									  \s""");
			return true;
		} else {
			IO.println("""
								\s
					╔══════════════════════════════╗
						  ✖  Game Over  ✖
					╚══════════════════════════════╝
								 \s""");
			return false;
		}
	}

	private void playHand() {
		var currentHand = new ArrayList<>(gameState.currentHand());
		var cardsToDraw = HANDSIZE - currentHand.size();

		currentHand.addAll(gameState.deck().draw(cardsToDraw));
		gameState.setCurrentHand(currentHand);

		IO.println("Cartes piochées :");
		for (var i = 0; i < currentHand.size(); i++) {
			IO.println(i + " - " + currentHand.get(i));
		}

		var selectedCards = selectCards(currentHand);

		var hand = new Hand(selectedCards);
		IO.println(hand);

		var combination = HandEvaluator.evaluate(hand);
		var score = Combination.computeScore(combination);

		IO.println("Combinaison: " + combination.getClass().getSimpleName());
		IO.println("Score obtenu: " + score);

		var remainingCards = new ArrayList<>(currentHand);
		remainingCards.removeAll(selectedCards);
		gameState.deck().addToDiscard(selectedCards);
		gameState.setCurrentHand(remainingCards);

		var oldPlayer = gameState.playerState();
		var updatedPlayer = new PlayerState(oldPlayer.name(), oldPlayer.totalScore() + score,
				oldPlayer.handCount() - 1);

		gameState.updatePlayerState(updatedPlayer);

		IO.println(gameState.playerState());
	}

	private List<Card> selectCards(List<Card> drawnCards) {
		var selected = new ArrayList<Card>();

		IO.println("""
				Choisissez entre 1 et 5 cartes.
				Tapez -1 pour terminer.
				""");

		while (selected.size() < 5) {
			var index = scanner.nextInt();

			if (index == -1 && !selected.isEmpty()) {
				break;
			}

			if (index < 0 || index >= drawnCards.size()) {
				IO.println("Indice invalide.");
				continue;
			}

			var chosen = drawnCards.get(index);

			if (selected.contains(chosen)) {
				IO.println("Carte déjà choisie.");
				continue;
			}

			selected.add(chosen);
		}
		return selected;
	}

}
