package fr.uge.azathro.domain.types.combination;

public record FourOfAKind(int chips, int multiplier) implements Combination {
    public FourOfAKind {
        if(chips <=0 || multiplier <=0){
            throw new IllegalArgumentException();
        }
    }
}
