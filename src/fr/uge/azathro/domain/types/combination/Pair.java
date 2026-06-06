package fr.uge.azathro.domain.types.combination;

public record Pair(int chips, int multiplier) implements Combination {
    public Pair {
        if(chips <=0 || multiplier <=0){
            throw new IllegalArgumentException();
        }
    }
}
