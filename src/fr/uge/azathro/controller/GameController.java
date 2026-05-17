package fr.uge.azathro.controller;

import fr.uge.azathro.domain.Blind;
import fr.uge.azathro.domain.types.planet.Planet;
import fr.uge.azathro.model.GameState;
import fr.uge.azathro.model.PlayerState;
import fr.uge.azathro.domain.Card;
import fr.uge.azathro.domain.types.combination.*;
import fr.uge.azathro.domain.Hand;
import fr.uge.azathro.domain.Dealer;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import fr.uge.azathro.view.ConsoleView;

public class GameController {
    private static final int HAND_SIZE = 8;
    private final GameState gameState;
    private final Scanner scanner = new Scanner(System.in);
    private final ConsoleView view = new ConsoleView();

    public GameController(GameState gameState) {
        this.gameState = gameState;
    }

    public void startGame() {
        view.showIntro();
        while (!gameState.isFinished()) {
            if (!playBlind()) {
                return;
            }
            gameState.increaseBlindCount();
            var oldPlayer = gameState.playerState();
            var map = new HashMap<>(oldPlayer.planetDrawed());
            var drawPlanet = Planet.draw();
            view.showPlanetDrawn(drawPlanet);
            map.put(drawPlanet, map.getOrDefault(drawPlanet, 0) + 1);
            var newPlayer = new PlayerState(oldPlayer.name(), 0, PlayerState.HANDCOUNT, map);
            gameState.updatePlayerState(newPlayer);
            gameState.deck().createStandardDeck();
            var nextBlind = new Blind(gameState.currentBlind().score() * 2);
            gameState.updateCurrentBlind(nextBlind);
            gameState.setCurrentHand(List.of());
        }
        view.showGameEnd();
    }

    private boolean playBlind() {
        view.showGameState(gameState);
        view.showPlayerState(gameState.playerState());
        while (!gameState.currentBlind().isBlinded(gameState.playerState())
                && gameState.playerState().handCount() > 0) {
            playHand();
        }

        if (gameState.currentBlind().isBlinded(gameState.playerState())) {
            view.showBlindSuccess();
            return true;
        } else {
            view.showGameOver();
            return false;
        }
    }

    private void playHand() {
        var currentHand = new ArrayList<>(gameState.currentHand());
        var cardsToDraw = HAND_SIZE - currentHand.size();
        currentHand.addAll(gameState.deck().draw(cardsToDraw));
        gameState.setCurrentHand(currentHand);
        view.showDrawnCards(currentHand);
        var selectedCards = selectCards(currentHand);

        var hand = new Hand(selectedCards);
        view.showHand(hand);

        var combination = Dealer.evaluate(hand, gameState.playerState().planetDrawed());
        var score = Combination.computeScore(combination);

        view.showCombination(combination);
        view.showScore(score);

        var remainingCards = new ArrayList<>(currentHand);
        remainingCards.removeAll(selectedCards);
        gameState.deck().addToDiscard(selectedCards);
        gameState.setCurrentHand(remainingCards);

        var oldPlayer = gameState.playerState();
        var updatedPlayer = new PlayerState(oldPlayer.name(), oldPlayer.totalScore() + score,
                oldPlayer.handCount() - 1, oldPlayer.planetDrawed());
        gameState.updatePlayerState(updatedPlayer);
        view.showPlayerState(gameState.playerState());
    }

    private List<Card> selectCards(List<Card> drawnCards) {
        var selected = new ArrayList<Card>();
        view.showSelectCardsPrompt();
        while (true) {
            if (selected.size() == 5) {
                break;
            }
            var index = scanner.nextInt();
            if (index == -1 && !selected.isEmpty()) {
                break;
            }
            if (index < 0 || index >= drawnCards.size()) {
                view.showInvalidIndex();
                continue;
            }
            var chosen = drawnCards.get(index);

            if (selected.contains(chosen)) {
                view.showCardAlreadyChosen();
                continue;
            }
            selected.add(chosen);
        }
        return selected;
    }

}

