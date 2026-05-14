package fr.uge.azathro.domain.types.planet;

public record Saturne() implements Planet {
	
	@Override
	public int bonusChips() {
		return 15;
	}
	
	@Override
	public int bonusMult() {
		return 2;
	}
}
