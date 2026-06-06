package fr.uge.azathro.domain.types.combination;

public record FullHouse(int chips, int multiplier) implements Combination {
    public FullHouse {
        if(chips <=0 || multiplier <=0){
            throw new IllegalArgumentException();
        }
    }
}
