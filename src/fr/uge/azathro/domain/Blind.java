package fr.uge.azathro.domain;

import fr.uge.azathro.model.PlayerState;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public record Blind(String name, double score) {
    private static final List<String> BLIND_NAMES = List.of(
            "Le Défi de l'Abîme",
            "Le Jugement d'Azathro",
            "La Malédiction du Sphinx",
            "L'Épreuve des Anciens",
            "Le Pacte du Néant",
            "La Danse des Ombres",
            "Le Labyrinthe des Illusions",
            "Le Rêve du Chaos",
            "La Quête de l'Éternité",
            "Le Sceau de l'Inconnu"
    );

    public Blind {
        Objects.requireNonNull(name);
        if (score < 0) {
            throw new IllegalArgumentException("Le score du blind doit être positif");
        }

    }

    public Blind(double score) {
        this(BLIND_NAMES.get(new Random().nextInt(BLIND_NAMES.size())), score);
    }

    public boolean isBlinded(PlayerState state) {
        Objects.requireNonNull(state);
        return state.totalScore() >= score;
    }


    @Override
    public String toString() {
        return """
                \s
                   ~~~  %s  ~~~
                  \s
                   ⚝ Seuil: %.2f ⚝
                  \s
                   "Azathro te lance un défi !"
                \s
                \s""".formatted(name, score);
    }
}
