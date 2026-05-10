package fr.uge.azathro.domain.types.rank;

public record King(int value) implements Rank {
    public King(){
        this(13);
    }


    @Override
    public String toString() {
        return "King";
    }
}
