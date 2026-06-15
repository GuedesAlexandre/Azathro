package fr.uge.azathro.domain.types.combination;

public sealed interface Combination permits Flush, FourOfAKind, FullHouse, HighCard, Pair, Straight, StraightFlush, ThreeOfAKind, TwoPair {
    static int computeScore(Combination combination) {
        return combination.chips() * combination.multiplier();
    }

    int chips();

    int multiplier();
}
