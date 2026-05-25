package fr.uge.azathro;

import fr.uge.azathro.domain.Blind;
import fr.uge.azathro.domain.Deck;
import fr.uge.azathro.model.GameState;
import fr.uge.azathro.model.PlayerState;
import fr.uge.azathro.controller.GameController;
import fr.uge.azathro.view.ConsoleView;
import fr.uge.azathro.view.GraphicView;
import fr.uge.azathro.view.View;

import java.util.Arrays;

public class Main {
    static void main(String[] args){
        var deck = new Deck();
        var blind = new Blind(10L);
        var playerState = new PlayerState("Le fameux joueur pro");
        var gameState = new GameState(blind, deck, playerState);

        boolean graphic = Arrays.asList(args).contains("--graphic");
        View view;

        if (graphic) {
            view = new GraphicView(14, 50);
        } else {
            view = new ConsoleView();
        }

        var controller = new GameController(gameState, view);
        controller.start();
    }
}
