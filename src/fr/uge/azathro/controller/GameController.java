package fr.uge.azathro.controller;

import com.github.forax.zen.Application;
import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;
import fr.uge.azathro.controller.engine.GameEngine;
import fr.uge.azathro.domain.Hand;
import fr.uge.azathro.model.GameState;
import fr.uge.azathro.view.ConsoleView;
import fr.uge.azathro.view.GraphicView;
import fr.uge.azathro.view.View;

import java.awt.*;
import java.util.*;

public final class GameController {
    private final GameEngine engine;
    private final View view;
    private final Set<Integer> selectedIndexes = new HashSet<>();
    private String lastCombination;
    private Integer lastScore;
    private final Scanner scanner = new Scanner(System.in);

    public GameController(GameState gameState, View view) {
        Objects.requireNonNull(gameState);
        Objects.requireNonNull(view);
        this.engine = new GameEngine(gameState);
        this.view = view;
    }

    public void start() {
        switch(view){
            case GraphicView gv -> Application.run(Color.DARK_GRAY, context -> graphicLoop(context, gv));
            case ConsoleView cv -> consoleLoop(cv);
        }
    }

    private void graphicLoop(ApplicationContext context, GraphicView graphicView) {
        graphicView.setContext(context);
        view.showIntro();
        while (engine.gameState().isFinished()) {
            if (engine.isGameOver()) {
                view.showGameOver();
                context.dispose();
                return;
            }
            engine.ensureHandFilled();
            view.showGameState(engine.gameState(), selectedIndexes, lastCombination, lastScore);

            var event = context.pollOrWaitEvent(10);
            if (event == null) continue;

            switch (event) {
                case KeyboardEvent ke -> {
                    if (ke.key() == KeyboardEvent.Key.ESCAPE) {
                        context.dispose();
                        return;
                    }
                    if (ke.key() == KeyboardEvent.Key.SPACE) {
                        playHand();
                    }
                    if (ke.key() == KeyboardEvent.Key.D) {
                        discardHand();
                    }
                }
                case PointerEvent pe -> {
                    if (pe.action() != PointerEvent.Action.POINTER_DOWN) continue;
                    var screen = context.getScreenInfo();
                    var handSize = engine.gameState().currentHand().size();
                    var helpRect = graphicView.getHelpButton();
                    if (helpRect.contains(pe.location().x(), pe.location().y())) {
                    	graphicView.toggleGuide();
                    	continue;
                    }
                    if (graphicView.isInsideCardArea(pe.location().x(), pe.location().y(), screen.width(), screen.height(), handSize)) {
                        toggleSelect(graphicView.cardIndexFromX(pe.location().x(), screen.width(), handSize));
                    }
                }
                default -> {}
            }

            checkBlindAndAdvance();
        }
        view.showGameEnd();
        context.dispose();
    }

    private void consoleLoop(ConsoleView consoleView) {
        view.showIntro();
        while (engine.gameState().isFinished()) {
            engine.ensureHandFilled();
            view.showGameState(engine.gameState(), selectedIndexes, lastCombination, lastScore);

            selectCardsConsole(consoleView);
            view.showGameState(engine.gameState(), selectedIndexes, lastCombination, lastScore);

            consoleView.showActionPrompt();
            var action = scanner.next().trim().toUpperCase();
            if (action.equals("D")) {
                discardHand();
            } else {
                playHand();
            }

            if (engine.isGameOver()) {
                view.showGameOver();
                return;
            }

            checkBlindAndAdvance();
        }
        view.showGameEnd();
    }

    private void toggleSelect(int index) {
        if (index < 0 || index >= engine.gameState().currentHand().size()) return;
        if (selectedIndexes.contains(index)) {
            selectedIndexes.remove(index);
        } else if (selectedIndexes.size() < 5) {
            selectedIndexes.add(index);
        }
    }

    private void playHand() {
        if (selectedIndexes.isEmpty()) return;
        var cards = Hand.fromIndexes(selectedIndexes, engine.gameState().currentHand()).cards();
        var combination = engine.evaluateHand(cards);
        lastScore = engine.playHand(cards);
        lastCombination = combination.getClass().getSimpleName();
        selectedIndexes.clear();
    }

    private void discardHand() {
        if (selectedIndexes.isEmpty()) return;
        var cards = Hand.fromIndexes(selectedIndexes, engine.gameState().currentHand()).cards();
        engine.discardActiveCards(cards);
        lastCombination = "Défausse";
        lastScore = null;
        selectedIndexes.clear();
    }

    private void checkBlindAndAdvance() {
        if (engine.isBlindCompleted()) {
            view.showBlindSuccess();
            var planet = engine.advanceToNextBlind();
            view.showPlanetDrawn(planet);
        }
    }

    private void selectCardsConsole(ConsoleView consoleView) {
        selectedIndexes.clear();
        consoleView.showSelectCardsPrompt();
        while (selectedIndexes.size() < 5) {
            int index = scanner.nextInt();
            if (index == -1 && !selectedIndexes.isEmpty()) break;
            if (index < 0 || index >= engine.gameState().currentHand().size()) {
                consoleView.showInvalidIndex();
                continue;
            }
            if (selectedIndexes.contains(index)) {
                consoleView.showCardAlreadyChosen();
                continue;
            }
            selectedIndexes.add(index);
        }
    }
}