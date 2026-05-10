package main.java.fr.uge.azathro.domain;

import main.java.fr.uge.azathro.model.PlayerState;

import java.util.Objects;

public record Blind(String name, Long score) {
    public Blind{
        Objects.requireNonNull(name);
        Objects.requireNonNull(score);
    }

    public boolean isBlinded(PlayerState  state) {
        Objects.requireNonNull(state);
        return state.score() >= score;
    }
}
