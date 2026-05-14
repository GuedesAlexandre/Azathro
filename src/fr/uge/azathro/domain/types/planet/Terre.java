package fr.uge.azathro.domain.types.planet;

public record Terre() implements Planet {
	
	@Override
	public int bonusChips() {
		return 25;
	}
	
	@Override
	public int bonusMult() {
		return 2;
	}
}
