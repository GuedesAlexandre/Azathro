package fr.uge.azathro.domain.types.combination;

public record TwoPair(int chips, int multiplier) implements Combination {
    public TwoPair {
        if(chips <=0 || multiplier <=0){
            throw new IllegalArgumentException();
        }
    }
}
