package fr.uge.azathro.domain;

import fr.uge.azathro.model.PlayerState;

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


    @Override
    public String toString() {
        return """
           \s
              ~~~  %s  ~~~
             \s
              ⚝ Seuil: %d ⚝
             \s
              "Azathro te lance un défi !"
           \s
           \s""".formatted(name, score);
    }
}
