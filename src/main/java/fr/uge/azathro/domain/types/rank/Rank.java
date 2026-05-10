package main.java.fr.uge.azathro.domain.types.rank;

public sealed  interface Rank permits Ace, Jack, King, NumericRank, Queen {
    int value();

}
