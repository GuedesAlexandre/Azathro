package fr.uge.azathro.view;

import fr.uge.azathro.domain.Card;
import fr.uge.azathro.domain.types.planet.Planet;
import fr.uge.azathro.model.GameState;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

public final class ConsoleView implements View {
	private static final String RED = "\u001B[31m";
	private static final String YELLOW = "\u001B[33m";
	private static final String GREEN = "\u001B[32m";
	private static final String CYAN = "\u001B[36m";
	private static final String RESET = "\u001B[0m";
	
    @Override
    public void showIntro() {
        IO.println("""
                
                ════════════════════════════════════════
                     ✦ ･ﾟ･ ｡ ･ﾟ･ ✦ ･ﾟ･ ｡ ･ﾟ･ ✦
                
                        ～ A Z A T H R O ～
                
                        Les cartes des abysses
                
                     ✦ ･ﾟ･ ｡ ･ﾟ･ ✦ ･ﾟ･ ｡ ･ﾟ･ ✦
                ════════════════════════════════════════
                
                """);
    }

    @Override
    public void showGameState(GameState gameState, Set<Integer> selectedIndexes, String lastCombination, Integer lastScore) {
        Objects.requireNonNull(gameState);
        Objects.requireNonNull(selectedIndexes);
        if (lastCombination != null) {
            var msg = lastScore != null
                    ? "Derniere combinaison jouee : " + lastCombination + " (Score : " + lastScore + ")"
                    : lastCombination + " effectuee";
            IO.println(msg);
        }
        IO.println(gameState);
        IO.println(gameState.playerState());
        showHandWithSelection(gameState.currentHand(), selectedIndexes);
        showPlanets(gameState);
    }

    private void showHandWithSelection(List<Card> cards, Set<Integer> selectedIndexes) {
        Objects.requireNonNull(cards);
        Objects.requireNonNull(selectedIndexes);
        IO.println("Main du joueur :");
        IntStream.range(0, cards.size())
                .forEach(i -> {
                    String prefix = selectedIndexes.contains(i) ? "[X] " : "[ ] ";
                    IO.println(prefix + i + ": " + coloredCard(cards.get(i)));
                });
    }

    @Override
    public void showPlanetDrawn(Planet planet) {
        Objects.requireNonNull(planet);
        IO.println("Planete tire : " + planet + " (" + planet.combination() + " bonus : "
                + planet.bonusChips() + " chips, x" + planet.bonusMult() + " multiplicateur)");
    }

    @Override
    public void showGameEnd() {
        IO.println("""
                			\s
                ╔══════════════════════════════╗
                     ⚝  Partie terminée !  ⚝
                ╚══════════════════════════════╝
                			  \s""");
    }

    @Override
    public void showBlindSuccess() {
        IO.println("""
                				\s
                ╔══════════════════════════════╗
                      ⚝  Blind réussi !  ⚝
                ╚══════════════════════════════╝
                				  \s""");
    }

    @Override
    public void showGameOver() {
        IO.println("""
                			\s
                ╔══════════════════════════════╗
                	  ✖  Game Over  ✖
                ╚══════════════════════════════╝
                			 \s""");
    }

    public void showSelectCardsPrompt() {
        IO.println("""
        		
                Choisissez entre 1 et 5 cartes.
                Tapez -1 pour terminer.
                
                """);
    }

    public void showInvalidIndex() {
        IO.println("Indice invalide.");
    }

    public void showCardAlreadyChosen() {
        IO.println("Carte deja choisie.");
    }

    public void showActionPrompt() {
        IO.println("""

                Action ? J = Jouer | D = Défausser

                """);
    }
    
    private void showPlanets(GameState gameState) {
        var planets = gameState.playerState().planetDrawed();
        IO.println("Planètes possédées : ");
        if (planets.isEmpty()) {
            IO.println("Aucune planète.");
        }
        planets.forEach((planet, level) -> {
            IO.println(
                "- " + planet.name()
                + " [" + planet.combination() + "]"
                + " x" + level
            );
        });
    }
    
    private String coloredCard(Card card) {
        var suit = card.suit();
        var color = switch (suit) {
            case HEART -> RED;
            case DIAMOND -> YELLOW;
            case CLUB -> GREEN;
            case SPADE -> CYAN;
        };
        return color + card + RESET;
    }
    
}
