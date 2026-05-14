package fr.uge.azathro.domain.types.planet;

public record Neptune() implements Planet {
	
	@Override
	public int bonusChips() {
		return 40;
	}
	
	@Override
	public int bonusMult() {
		return 4;
	}
}
