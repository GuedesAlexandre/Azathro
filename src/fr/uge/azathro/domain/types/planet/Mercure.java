package fr.uge.azathro.domain.types.planet;

public record Mercure() implements Planet {
	
	@Override
	public int bonusChips() {
		return 15;
	}
	
	@Override
	public int bonusMult() {
		return 1;
	}
}
