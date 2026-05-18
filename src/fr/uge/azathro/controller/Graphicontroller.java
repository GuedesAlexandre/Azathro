package fr.uge.azathro.controller;

import com.github.forax.zen.Application;
import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;

import fr.uge.azathro.controller.engine.GameEngine;
import fr.uge.azathro.domain.Hand;
import fr.uge.azathro.model.GameState;
import fr.uge.azathro.view.GraphicView;
import fr.uge.azathro.view.GraphicViewState;

import java.awt.Color;
import java.util.*;

public final class Graphicontroller {
    private final GameEngine engine;
    private final GraphicView view;
    private final Set<Integer> selectedIndexes = new HashSet<>();
    private String lastCombination;
    private Integer lastScore;
    private boolean gameOver;

    public Graphicontroller(GameState gameState) {
        this.engine = new GameEngine(gameState);
        this.view = new GraphicView(220, 300, 14, 50);
    }

    public void start() {
        Application.run(Color.DARK_GRAY, this::loop);
    }

    private void loop(ApplicationContext context) {
        while (true) {
            if (gameOver || engine.gameState().isFinished()) {
              context.dispose();
              return;
            }
            engine.ensureHandFilled();
            render(context);
            var event = context.pollOrWaitEvent(10);
            if (event == null) {
                continue;
            }
            switch (event) {
                case KeyboardEvent ke -> {
                    if (ke.key() == KeyboardEvent.Key.ESCAPE) {
                        context.dispose();
                        return;
                    }
                    if (ke.key() == KeyboardEvent.Key.SPACE) {
                        playSelectedHand();
                    }
                }
                case PointerEvent pe -> {
                    if (pe.action() != PointerEvent.Action.POINTER_DOWN) {
                        continue;
                    }
                    var screen = context.getScreenInfo();
                    var width = screen.width();
                    var height = screen.height();
                    var locationOfPointer = pe.location();
                    var handSize = engine.gameState().currentHand().size();

                    if (view.isInsideCardArea(locationOfPointer.x(), locationOfPointer.y(), width, height, handSize)) {
                        int index = view.cardIndexFromX(locationOfPointer.x(), width, handSize);
                        toggleSelect(index);
                    }
                }
                default -> {
                }
            }
        }
    }

    private void toggleSelect(int index) {
        if (index < 0 || index >= engine.gameState().currentHand().size()) {
            return;
        }
        if (selectedIndexes.contains(index)) {
            selectedIndexes.remove(index);
        } else if (selectedIndexes.size() < 5) {
            selectedIndexes.add(index);
        }
    }

    private void playSelectedHand() {
        if (selectedIndexes.isEmpty()) {
            return;
        }

        var hand = Hand.fromIndexes(selectedIndexes, engine.gameState().currentHand());
        var combination = engine.evaluateHand(hand.cards());
        var score = engine.playHand(hand.cards());

        lastCombination = combination.getClass().getSimpleName();
        lastScore = score;

        if (engine.isGameOver()) {
            gameOver = true;
            return;
        }

        selectedIndexes.clear();

        if (engine.isBlindCompleted()) {
            engine.advanceToNextBlind();
        }
    }

    private void render(ApplicationContext context) {
        var state = new GraphicViewState(
                engine.gameState().currentHand(),
                engine.gameState().currentBlind(),
                engine.gameState().deck().size(),
                0,
                engine.gameState().playerState(),
                Set.copyOf(selectedIndexes),
                lastCombination,
                lastScore
        );
        view.draw(context, state);
    }
}