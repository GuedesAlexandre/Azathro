package fr.uge.azathro.domain.types.combination;

public record Flush(int chips, int multiplier) implements Combination {
    public Flush{
        if(chips <=0 || multiplier <=0){
            throw new IllegalArgumentException();
        }
    }

}
