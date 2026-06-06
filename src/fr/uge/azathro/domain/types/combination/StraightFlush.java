package fr.uge.azathro.domain.types.combination;

public record StraightFlush(int chips, int multiplier) implements Combination {
    public StraightFlush {
        if(chips <=0 || multiplier <=0){
            throw new IllegalArgumentException();
        }
    }
}
