package fr.uge.azathro.domain.types.combination;

public record ThreeOfAKind(int chips, int multiplier) implements Combination {
    public ThreeOfAKind {
        if(chips <=0 || multiplier <=0){
            throw new IllegalArgumentException();
        }
    }
}
