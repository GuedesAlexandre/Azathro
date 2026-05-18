package fr.uge.azathro.domain.types.planet;

import java.util.Random;

public enum Planet {
    PLUTON("Carte Haute",10, 1),
    MERCURE("Paire",15, 1),
    URANUS("Double Paire",20, 1),
    VENUS("Brelan",20, 2),
    SATURNE("Suite",30, 3),
    JUPITER("Couleur",15, 2),
    TERRE("Full",25, 2),
    MARS("Carré",30, 3),
    NEPTUNE("Quinte Flush",40, 4);
    private final String combination;
    private final int bonusChips;
    private final int bonusMult;


    Planet(String combination, int bonusChips, int bonusMult) {
        this.combination = combination;
        this.bonusChips = bonusChips;
        this.bonusMult = bonusMult;
    }

    public String combination() {
        return combination;
    }

    public int bonusChips() {
        return bonusChips;
    }

    public int bonusMult() {
        return bonusMult;
    }

    public static Planet draw(){
        var planet = Planet.values();
        return planet[new Random().nextInt(planet.length)];
    }


}