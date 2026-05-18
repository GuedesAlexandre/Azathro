package fr.uge.azathro;

import fr.uge.azathro.domain.Blind;
import fr.uge.azathro.domain.Deck;
import fr.uge.azathro.model.GameState;
import fr.uge.azathro.model.PlayerState;
import fr.uge.azathro.controller.*;


import java.util.Arrays;

public class Main {
    static void main(String[] args){
        var deck = new Deck();
        var blind = new Blind(10L);
        var playerState = new PlayerState("Le fameux joueur pro");
        var gameState = new GameState(blind, deck, playerState);

        boolean graphic = Arrays.asList(args).contains("--graphic");

        if (graphic) {
            var controller = new Graphicontroller(gameState);
            controller.start();
        } else {
            var controller = new GameController(gameState);
            controller.startGame();
        }
    }



}
