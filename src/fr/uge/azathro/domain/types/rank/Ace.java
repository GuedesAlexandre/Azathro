package fr.uge.azathro.domain.types.rank;

public record Ace(int value) implements Rank {
    public Ace(){
       this(14);
    }
    @Override
    public String toString() {
        return "Ace";
    }
}
