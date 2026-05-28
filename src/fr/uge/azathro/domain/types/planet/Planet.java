package fr.uge.azathro.domain.types.planet;

import java.util.Random;

public enum Planet {
    PLUTON("High card",10, 1),
    MERCURE("Pair",15, 1),
    URANUS("Two Pair",20, 1),
    VENUS("Three of a kind",20, 2),
    SATURNE("Straight",30, 3),
    JUPITER("Flush",15, 2),
    TERRE("Full House",25, 2),
    MARS("Four of a kind",30, 3),
    NEPTUNE("Straight Flush",40, 4);
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