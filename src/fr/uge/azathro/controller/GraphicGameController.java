package fr.uge.azathro.controller;

import com.github.forax.zen.Application;
import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;

import fr.uge.azathro.domain.Blind;
import fr.uge.azathro.domain.Hand;
import fr.uge.azathro.domain.Dealer;
import fr.uge.azathro.domain.types.combination.Combination;
import fr.uge.azathro.domain.types.planet.Planet;
import fr.uge.azathro.model.GameState;
import fr.uge.azathro.model.PlayerState;
import fr.uge.azathro.view.GraphicView;
import fr.uge.azathro.view.GraphicViewState;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.awt.Color;
import java.io.File;
import java.util.*;

public final class GraphicGameController {
    private static final int HAND_SIZE = 8;
    private final GameState gameState;
    private final GraphicView view;
    private final Set<Integer> selectedIndexes = new HashSet<>();
    private String lastCombination;
    private Integer lastScore;
    private boolean gameOver;


    public GraphicGameController(GameState gameState) {
        this.gameState = gameState;
        this.view = new GraphicView(220, 300, 14, 50);
    }

    private static Clip playLoop() throws Exception {
        try (var audio = AudioSystem.getAudioInputStream(new File("src/fr/uge/azathro/view/assets/bgsound.wav"))) {
            var clip = AudioSystem.getClip();
            clip.open(audio);

            var gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-30.0f);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

            return clip;
        }
    }


    public void start() {
        Clip bgClip;
        try {
            bgClip = playLoop();
        } catch (Exception e) {
            throw new RuntimeException("Failed to play background music", e);
        }
        Application.run(Color.DARK_GRAY, this::loop);
        bgClip.stop();
        bgClip.close();
    }

    private void loop(ApplicationContext context) {
        while (true) {
            if (gameOver || gameState.isFinished()) {
                context.dispose();
                return;
            }
            ensureHandFilled();
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
                    var location = pe.location();
                    var handSize = gameState.currentHand().size();

                    if (view.isInsideCardArea(location.x(), location.y(), width, height, handSize)) {
                        int index = view.cardIndexFromX(location.x(), width, handSize);
                        toggleSelect(index);
                    }
                }
                default -> {
                }
            }
        }
    }

    private void ensureHandFilled() {
        var missing = HAND_SIZE - gameState.currentHand().size();
        if (missing <= 0) {
            return;
        }
        var filled = new ArrayList<>(gameState.currentHand());
        filled.addAll(gameState.deck().draw(missing));
        gameState.setCurrentHand(filled);
    }

    private void toggleSelect(int index) {
        if (index < 0 || index >= gameState.currentHand().size()) {
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
        var cards = selectedIndexes.stream()
                .sorted()
                .map(index -> gameState.currentHand().get(index))
                .toList();

        var hand = new Hand(cards);
        var combination = Dealer.evaluate(hand, gameState.playerState().planetDrawed());
        var score = Combination.computeScore(combination);
        lastCombination = combination.getClass().getSimpleName();
        lastScore = score;

        var oldPlayer = gameState.playerState();
        var updatedPlayer = new PlayerState(
                oldPlayer.name(),
                oldPlayer.totalScore() + score,
                oldPlayer.handCount() - 1,
                oldPlayer.planetDrawed()
        );
        gameState.updatePlayerState(updatedPlayer);

        if (!gameState.currentBlind().isBlinded(updatedPlayer) && updatedPlayer.handCount() <= 0) {
            gameOver = true;
            return;
        }

        var remaining = gameState.currentHand().stream()
                .filter(c -> !cards.contains(c))
                .toList();

        gameState.setCurrentHand(remaining);
        selectedIndexes.clear();

        if (gameState.currentBlind().isBlinded(updatedPlayer)) {
            advanceBlind(updatedPlayer);
        } else {
            ensureHandFilled();
        }
    }

    private void advanceBlind(PlayerState player) {
        var map = new HashMap<>(player.planetDrawed());
        var drawPlanet = Planet.draw();
        map.put(drawPlanet, map.getOrDefault(drawPlanet, 0) + 1);
        var newPlayer = new PlayerState(player.name(), 0, PlayerState.HANDCOUNT, map);
        gameState.updatePlayerState(newPlayer);
        gameState.increaseBlindCount();
        gameState.deck().createStandardDeck();
        gameState.setCurrentHand(List.of());
        var nextBlind = new Blind(gameState.currentBlind().score() * 2);
        gameState.updateCurrentBlind(nextBlind);
    }

    private void render(ApplicationContext context) {
        var state = new GraphicViewState(
                gameState.currentHand(),
                gameState.currentBlind(),
                gameState.deck().size(),
                0,
                gameState.playerState(),
                Set.copyOf(selectedIndexes),
                lastCombination,
                lastScore
        );
        view.draw(context, state);
    }
}