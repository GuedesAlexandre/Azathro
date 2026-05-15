package fr.uge.azathro.domain.types.planet;

public record Jupiter() implements Planet {
	
	@Override
	public int bonusChips() {
		return 30;
	}
	
	@Override
	public int bonusMult() {
		return 3;
	}
}
