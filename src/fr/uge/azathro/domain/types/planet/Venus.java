package fr.uge.azathro.domain.types.planet;

public record Venus() implements Planet {
	
	@Override
	public int bonusChips() {
		return 20;
	}
	
	@Override
	public int bonusMult() {
		return 2;
	}
}
