package fr.uge.azathro.controller.engine;

import fr.uge.azathro.domain.Blind;
import fr.uge.azathro.domain.Card;
import fr.uge.azathro.domain.Dealer;
import fr.uge.azathro.domain.Hand;
import fr.uge.azathro.domain.types.combination.Combination;
import fr.uge.azathro.domain.types.planet.Planet;
import fr.uge.azathro.model.GameState;
import fr.uge.azathro.model.PlayerState;

import java.util.HashMap;
import java.util.List;

public record GameEngine(GameState gameState) {
    private static final int HAND_SIZE = 8;

    public void ensureHandFilled() {
        gameState.fillHandToSize(HAND_SIZE);
    }

    public int playHand(List<Card> selectedCards) {
        var hand = new Hand(selectedCards);
        var combination = Dealer.evaluate(hand, gameState.playerState().planetDrawed());
        var score = Combination.computeScore(combination);
        gameState.removeCardsFromHand(selectedCards);
        gameState.discardCards(selectedCards);

        var updatedPlayer = gameState.playerState().withScoreAndDecrementHand(score);
        gameState.updatePlayerState(updatedPlayer);

        return score;
    }

    public Combination evaluateHand(List<Card> selectedCards) {
        var hand = new Hand(selectedCards);
        return Dealer.evaluate(hand, gameState.playerState().planetDrawed());
    }

    public boolean isBlindCompleted() {
        return gameState.currentBlind().isBlinded(gameState.playerState());
    }

    public boolean isGameOver() {
        return !isBlindCompleted() && gameState.playerState().hasNoHandsLeft();
    }

    public Planet advanceToNextBlind() {
        gameState.increaseBlindCount();

        var oldPlayer = gameState.playerState();
        var map = new HashMap<>(oldPlayer.planetDrawed());
        var drawPlanet = Planet.draw();
        map.put(drawPlanet, map.getOrDefault(drawPlanet, 0) + 1);

        var newPlayer = new PlayerState(
                oldPlayer.name(),
                0,
                PlayerState.HANDCOUNT,
                map
        );
        gameState.updatePlayerState(newPlayer);

        gameState.deck().createStandardDeck();
        var nextBlind = new Blind(gameState.currentBlind().score() * 1.5);
        gameState.updateCurrentBlind(nextBlind);
        gameState.setCurrentHand(List.of());

        return drawPlanet;
    }
}