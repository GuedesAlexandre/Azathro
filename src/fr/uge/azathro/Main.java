package fr.uge.azathro;
import fr.uge.azathro.domain.Blind;
import fr.uge.azathro.domain.Deck;
import fr.uge.azathro.domain.Hand;
import fr.uge.azathro.model.GameState;
import fr.uge.azathro.model.PlayerState;
import fr.uge.azathro.controller.*;

public class Main {
    static void main() {
        IO.println("""
                
                ════════════════════════════════════════
                     ✦ ･ﾟ･ ｡ ･ﾟ･ ✦ ･ﾟ･ ｡ ･ﾟ･ ✦
                
                        ～ A Z A T H R O ～
                
                        Les cartes des abysses
                
                     ✦ ･ﾟ･ ｡ ･ﾟ･ ✦ ･ﾟ･ ｡ ･ﾟ･ ✦
                ════════════════════════════════════════
                
                """);
        var deck = new Deck();
        var blind = new Blind("Small Blind", 100L);
        var playerState = new PlayerState("Alice", 0, 4);
        var gameState = new GameState(blind, deck, playerState);
        var controller = new GameController(gameState);
        controller.startGame();
    }
}
