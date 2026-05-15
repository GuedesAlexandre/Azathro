package fr.uge.azathro.model;

import java.util.Objects;

public record PlayerState(String name, int totalScore, int handCount) {
	public static final int HANDCOUNT = 4;
	
	public PlayerState(String name) {
		Objects.requireNonNull(name);
		this(name, 0, HANDCOUNT);
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
