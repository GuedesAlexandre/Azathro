package fr.uge.azathro.domain.types.combination;

public sealed interface Combination permits Flush, FourOfAKind, FullHouse, HighCard, Pair, Straight, StraightFlush, ThreeOfAKind, TwoPair {
    static int computeScore(Combination combination) {
        return switch (combination) {
            case HighCard hc -> 5;
            case Pair p -> 10 * 2;
            case TwoPair tp -> 20 * 2;
            case ThreeOfAKind tok -> 30 * 3;
            case Straight s -> 30 * 4;
            case Flush f -> 35 * 4;
            case FullHouse fh -> 40 * 4;
            case FourOfAKind fok -> 60 * 7;
            case StraightFlush sf -> 100 * 8;
        };
    }
}
