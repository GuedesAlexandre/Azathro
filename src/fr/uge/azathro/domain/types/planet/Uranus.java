package fr.uge.azathro.domain.types.planet;

public record Uranus() implements Planet {
	
	@Override
	public int bonusChips() {
		return 20;
	}
	
	@Override
	public int bonusMult() {
		return 1;
	}
}
