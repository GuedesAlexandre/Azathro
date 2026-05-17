package fr.uge.azathro;

import fr.uge.azathro.domain.Blind;
import fr.uge.azathro.domain.Deck;
import fr.uge.azathro.model.GameState;
import fr.uge.azathro.model.PlayerState;
import fr.uge.azathro.controller.*;


import java.util.Arrays;
import java.util.HashMap;

public class Main {
    static void main(String[] args){
        var deck = new Deck();
        var blind = new Blind(100L);
        var playerState = new PlayerState("", 0, PlayerState.HANDCOUNT, new HashMap<>());
        var gameState = new GameState(blind, deck, playerState);

        boolean graphic = Arrays.asList(args).contains("--graphic");

        if (graphic) {
            var controller = new GraphicGameController(gameState);
            controller.start();
        } else {
            var controller = new GameController(gameState);
            controller.startGame();
        }
    }



}
