package fr.uge.azathro.model;

import java.util.Objects;

public record PlayerState(String name, int totalScore, int handCount) {
	
	public PlayerState {
		Objects.requireNonNull(name);
		if (totalScore < 0) {
			throw new IllegalArgumentException("Score must be positive");
		}
		if (handCount < 0) {
			throw new IllegalArgumentException("Number of hands remaining must be positive");
		}
	}
	
	
	@Override
	    public String toString() {
	        return """
	        ════════════════════════════════════
	                Joueur : %s
	                Score : %d
	                Mains restants : %d
	        ════════════════════════════════════
	                """.formatted(name, totalScore, handCount);
	    }
}
