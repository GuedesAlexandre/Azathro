package fr.uge.azathro.controller;

import fr.uge.azathro.model.GameState;
import fr.uge.azathro.domain.Card;
import fr.uge.azathro.domain.types.combination.*;
import fr.uge.azathro.domain.Hand;
import fr.uge.azathro.domain.HandEvaluator;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class GameController {
	private final GameState gameState;
	private final Scanner scanner = new Scanner(System.in);

	public GameController(GameState gameState) {
		this.gameState = gameState;
	}

	public void startGame() {
		IO.println(gameState);

		while (!gameState.blindCleared() && !gameState.isGameOver()) {
			playTurn();
		}

		if (gameState.blindCleared()) {
			IO.println("""
					\s
					     ⚝ Blind réussi! ⚝
					  \s""");
		} else {
			IO.println("""
					\s
				      Game over! 
				  \s""");
		}
	}

	private void playTurn() {
		List<Card> drawnCards = gameState.deck().draw(8);

		IO.println("Cartes piochées :");
		for (int i = 0; i < drawnCards.size(); i++) {
			IO.println(i + " - " + drawnCards.get(i));
		}
		
		List<Card> selectedCards = selectFiveCards(drawnCards);

		Hand hand = new Hand(selectedCards);
		IO.println(hand);

		Combination combination = HandEvaluator.evaluate(hand);

		int score = Combination.computeScore(combination);

		IO.println("Combinaison: " + combination.getClass().getSimpleName());
		IO.println("Score obtenu: " + score);

		gameState.addScore(score);
		gameState.decreaseHandsRemaining();

		IO.println(gameState);
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
