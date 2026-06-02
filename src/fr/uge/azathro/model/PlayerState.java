package fr.uge.azathro.model;

import fr.uge.azathro.domain.types.planet.Planet;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record PlayerState(String name, int totalScore, int handCount, Map<Planet, Integer> planetDrawed) {
    public static final int HANDCOUNT = 4;

    public PlayerState {
        Objects.requireNonNull(name);
    }

    public PlayerState(String name) {
        this(name, 0, HANDCOUNT, new HashMap<>());
    }

    @Override
    public String toString() {
        return """
                ════════════════════════════════════
                        Joueur : %s
                        Score : %d
                        Mains restants : %d
                ════════════════════════════════════
                       \s""".formatted(name, totalScore, handCount);
    }

    public PlayerState withScoreAndDecrementHand(int additionalScore) {
        return new PlayerState(
                name,
                totalScore + additionalScore,
                handCount - 1,
                planetDrawed
        );
    }

    public boolean hasNoHandsLeft() {
        return handCount <= 0;
    }

}
