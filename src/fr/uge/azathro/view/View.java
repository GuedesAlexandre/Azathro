// src/fr/uge/azathro/view/View.java
package fr.uge.azathro.view;

import fr.uge.azathro.model.GameState;
import fr.uge.azathro.domain.types.planet.Planet;
import java.util.Set;

public sealed interface View permits ConsoleView, GraphicView {
    void showIntro();
    void showGameState(GameState gameState, Set<Integer> selectedIndexes, String lastCombination, Integer lastScore);
    void showBlindSuccess();
    void showGameOver();
    void showPlanetDrawn(Planet planet);
    void showGameEnd();

}