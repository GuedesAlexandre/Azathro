package main.java.fr.uge.azathro.domain.types.rank;

public record Jack(int value) implements Rank {
    public Jack() {
        this(11);
    }

    @Override
    public String toString() {
        return "Jack";
    }
}
