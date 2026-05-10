package fr.uge.azathro.domain.types.rank;

public record NumericRank(int value) implements Rank {
    public NumericRank {
        if(value< 2 || value> 10) {
            throw new IllegalArgumentException("Value must be between 2 and 10");
        }
    }

    @Override
    public String toString() {
        return String.format("%d", value());
    }

}
