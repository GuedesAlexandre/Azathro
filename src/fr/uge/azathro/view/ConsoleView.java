package fr.uge.azathro.view;

import fr.uge.azathro.domain.Card;
import fr.uge.azathro.domain.Hand;
import fr.uge.azathro.domain.types.combination.Combination;
import fr.uge.azathro.domain.types.planet.Planet;
import fr.uge.azathro.model.GameState;
import fr.uge.azathro.model.PlayerState;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public final class ConsoleView implements View {
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

    public void showGameState(GameState gameState) {
        IO.println(gameState);
    }

    public void showPlayerState(PlayerState playerState) {
        IO.println(playerState);
    }

    public void showPlanetDrawn(Planet planet) {
        IO.println("Planete tire : " + planet + " (" + planet.combination() + " bonus : "
                + planet.bonusChips() + " chips, x" + planet.bonusMult() + " multiplicateur)");
    }

    public void showGameEnd() {
        IO.println("""
                			\s
                ╔══════════════════════════════╗
                     ⚝  Partie terminée !  ⚝
                ╚══════════════════════════════╝
                			  \s""");
    }

    public void showBlindSuccess() {
        IO.println("""
                				\s
                ╔══════════════════════════════╗
                      ⚝  Blind réussi !  ⚝
                ╚══════════════════════════════╝
                				  \s""");
    }

    public void showGameOver() {
        IO.println("""
                			\s
                ╔══════════════════════════════╗
                	  ✖  Game Over  ✖
                ╚══════════════════════════════╝
                			 \s""");
    }

    public void showDrawnCards(List<Card> cards) {
        IO.println("Cartes piochées :");
        IntStream.range(0, cards.size())
                .forEach(i -> IO.println("[" + i + "] " + cards.get(i)));
    }

    public void showHand(Hand hand) {
        IO.println(hand);
    }

    public void showCombination(Combination combination) {
        Objects.requireNonNull(combination);
        IO.println("Combinaison: " + combination.getClass().getSimpleName());
    }

    public void showScore(int score) {
        IO.println("Score obtenu: " + score);
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
}
