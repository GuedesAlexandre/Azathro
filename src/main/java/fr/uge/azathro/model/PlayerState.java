package main.java.fr.uge.azathro.model;

import main.java.fr.uge.azathro.domain.Hand;

public record PlayerState(String name, int score, Hand hand) {
}
