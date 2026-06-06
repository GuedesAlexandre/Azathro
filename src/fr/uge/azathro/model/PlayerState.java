package fr.uge.azathro.model;

import fr.uge.azathro.domain.types.planet.Planet;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record PlayerState(String name, int totalScore, int handCount, int discardCount, Map<Planet, Integer> planetDrawed) {
    public static final int HANDCOUNT = 4;
    public static final int DISCARDCOUNT = 3;

    public PlayerState {
        Objects.requireNonNull(name);
    }

    public PlayerState(String name) {
        this(name, 0, HANDCOUNT, DISCARDCOUNT, new HashMap<>());
    }

    @Override
    public String toString() {
        return """
                ════════════════════════════════════
                        Joueur : %s
                        Score : %d
                        Mains restants : %d
                        Défausses restantes : %d
                ════════════════════════════════════
                       \s""".formatted(name, totalScore, handCount, discardCount);
    }

    public PlayerState withScoreAndDecrementHand(int additionalScore) {
        if(additionalScore < 0) {
            throw new IllegalArgumentException();
        }
        return new PlayerState(
                name,
                totalScore + additionalScore,
                handCount - 1,
                discardCount,
                planetDrawed
        );
    }


    public PlayerState withDecrementDiscard() {
        return new PlayerState(
                name,
                totalScore,
                handCount,
                discardCount - 1,
                planetDrawed
        );
    }

    public boolean hasNoHandsLeft() {
        return handCount <= 0;
    }

    public boolean hasNoDiscardsLeft() {
        return discardCount <= 0;
    }

}
