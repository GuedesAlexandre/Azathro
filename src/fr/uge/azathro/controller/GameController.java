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
	private final GameState gameState;
	private final PlayerState playerState;
	private final Scanner scanner = new Scanner(System.in);

	public GameController(GameState gameState, PlayerState playerState) {
		this.gameState = gameState;
		this.playerState = playerState;
	}

	
	public void startGame() {
		IO.println(playerState);
		while (!gameState.isFinished()) {
			boolean success = playBlind();
			// blind failed -> game over
			if (!success) {
				return;
			}
			gameState.increaseBlindCount();
			playerState.resetScore();
			playerState.resetHands();
		}
		IO.println("Partie terminée! ");
	}
	

	private boolean playBlind() {
		IO.println(gameState);

		// while Blind isn't cleared and there're still remained rounds -> play round
		while (!gameState.currentBlind().isBlinded(playerState) && playerState.round() > 0) {
			playRound();
		}

		if (gameState.currentBlind().isBlinded(playerState)) {
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

	private void playRound() {
		List<Card> drawnCards = gameState.deck().draw(8); // Draw 8 cards from the deck

		IO.println("Cartes piochées :");
		for (int i = 0; i < drawnCards.size(); i++) {
			IO.println(i + " - " + drawnCards.get(i));
		}

		List<Card> selectedCards = selectFiveCards(drawnCards); // Select a hand of 5 cards to play

		Hand hand = new Hand(selectedCards);
		IO.println(hand);

		Combination combination = HandEvaluator.evaluate(hand);

		int score = Combination.computeScore(combination);

		IO.println("Combinaison: " + combination.getClass().getSimpleName());
		IO.println("Score obtenu: " + score);

		playerState.addScore(score);
		playerState.decreaseRounds();

		IO.println(playerState);
	}

	private List<Card> selectFiveCards(List<Card> drawnCards) {
		List<Card> selected = new ArrayList<>();

		IO.println("\nChoisissez 5 cartes en saisissant les indices correspondants: ");

		while (selected.size() < 5) {
			int index = scanner.nextInt();

			if (index < 0 || index >= drawnCards.size()) {
				IO.println("Indice invalide.");
				continue;
			}

			Card chosen = drawnCards.get(index);

			if (selected.contains(chosen)) {
				IO.println("Carte déjà choisie.");
				continue;
			}

			selected.add(chosen);
		}
		return selected;
	}

}
