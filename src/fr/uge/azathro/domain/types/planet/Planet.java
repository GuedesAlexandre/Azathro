package fr.uge.azathro.domain.types.planet;

import fr.uge.azathro.domain.types.combination.Combination;

public sealed interface Planet permits Pluton, Mercure, Uranus, Venus, Saturne, Jupiter, Terre, Mars, Neptune{
	int bonusChips();
	
	int bonusMult();
	
	// Combination targetCombination();
}
