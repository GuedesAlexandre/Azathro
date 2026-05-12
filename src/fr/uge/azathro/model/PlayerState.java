package fr.uge.azathro.model;

import fr.uge.azathro.domain.Hand;

public class PlayerState {
	private final String name;
	private int totalScore;
	private int round;

	public PlayerState(String name) {
		this.name = name;
		this.totalScore = 0;
		this.round = 4; // 6 rounds/blind
	}
	
	public String name() {
		return name;
	}
	
	public int totalScore() {
		return totalScore;
	}
	
	public int round() {
		return round;
	}
	
	public void addScore(int score) {
		totalScore += score;
	}
	
	public void decreaseRounds() {
		round--;
	}
	
	public void resetScore() {
		totalScore = 0;
	}
	
	
	public void resetHands() {
		round = 4; 
	}
	
	@Override
	    public String toString() {
	        return """
	        ════════════════════════════════════
	                Joueur : %s
	                Score : %d
	                Tours restants : %d
	        ════════════════════════════════════
	                """.formatted(name, totalScore, round);
	    }
}
