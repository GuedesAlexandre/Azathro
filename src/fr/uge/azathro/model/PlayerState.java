package fr.uge.azathro.model;

import fr.uge.azathro.domain.Hand;

public record PlayerState(String name, int score, Hand hand) {
}
