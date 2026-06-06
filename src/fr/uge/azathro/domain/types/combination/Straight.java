package fr.uge.azathro.domain.types.combination;

public record Straight(int chips, int multiplier) implements Combination {
    public Straight {
        if(chips <=0 || multiplier <=0){
            throw new IllegalArgumentException();
        }
    }
}
