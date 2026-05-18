package fr.uge.azathro.controller;

import fr.uge.azathro.controller.engine.GameEngine;
import fr.uge.azathro.model.GameState;
import fr.uge.azathro.domain.Card;
import fr.uge.azathro.domain.Hand;
import fr.uge.azathro.view.ConsoleView;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameController {
    private final GameEngine engine;
    private final Scanner scanner = new Scanner(System.in);
    private final ConsoleView view = new ConsoleView();

    public GameController(GameState gameState) {
        this.engine = new GameEngine(gameState);
    }

    public void startGame() {
        view.showIntro();
        while (!engine.gameState().isFinished()) {
            if (!playBlind()) {
                return;
            }
            var drawPlanet = engine.advanceToNextBlind();
            view.showPlanetDrawn(drawPlanet);
        }
        view.showGameEnd();
    }

    private boolean playBlind() {
        view.showGameState(engine.gameState());
        view.showPlayerState(engine.gameState().playerState());

        while (!engine.isBlindCompleted() && engine.gameState().playerState().handCount() > 0) {
            playHand();
        }

        if (engine.isBlindCompleted()) {
            view.showBlindSuccess();
            return true;
        } else {
            view.showGameOver();
            return false;
        }
    }

    private void playHand() {
        engine.ensureHandFilled();
        view.showDrawnCards(engine.gameState().currentHand());

        var selectedCards = selectCards(engine.gameState().currentHand());
        var hand = new Hand(selectedCards);
        view.showHand(hand);

        var combination = engine.evaluateHand(selectedCards);
        var score = engine.playHand(selectedCards);

        view.showCombination(combination);
        view.showScore(score);
        view.showPlayerState(engine.gameState().playerState());
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