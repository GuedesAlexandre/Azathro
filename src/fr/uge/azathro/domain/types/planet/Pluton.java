package fr.uge.azathro.domain.types.planet;

public record Pluton() implements Planet {
	
	@Override
	public int bonusChips() {
		return 10;
	}
	
	@Override
	public int bonusMult() {
		return 1;
	}
}
