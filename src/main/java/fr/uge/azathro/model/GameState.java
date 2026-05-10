package main.java.fr.uge.azathro.model;

import main.java.fr.uge.azathro.domain.Blind;
import main.java.fr.uge.azathro.domain.Deck;

public class GameState {
    private final Blind currentBlind;
    private static int blindCount = 0;
    private final Deck deck;

    public GameState(Blind currentBlind, Deck deck) {
        this.currentBlind = currentBlind;
        this.deck = deck;
    }

    public void increaseBlindCount() {
        blindCount++;
    }

    public boolean isFinish() {
        return blindCount == 5;
    }

    @Override
    public String toString() {
        return "blindCount=" + blindCount + System.lineSeparator() + currentBlind;

    }
}
