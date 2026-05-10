package main.java.fr.uge.azathro;

import main.java.fr.uge.azathro.domain.Blind;
import main.java.fr.uge.azathro.domain.Deck;
import main.java.fr.uge.azathro.domain.Hand;
import main.java.fr.uge.azathro.model.GameState;

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
        var hand = new Hand(deck.draw(8));
        var gameState = new GameState(new Blind("Small Blind", 10L), deck);
        IO.println(gameState);
        IO.println(hand);
    }
}
