package main.java.fr.uge.azathro.domain.types.rank;

public record Queen(int value) implements Rank {
    public Queen() {
        this(12);
    }

    @Override
    public String toString() {
        return "Queen";
    }
}
