package fr.uge.azathro.domain.types.combination;

public sealed interface Combination permits Flush, FourOfAKind, FullHouse, HighCard, Pair, Straight, StraightFlush, ThreeOfAKind, TwoPair {
    static int computeScore(Combination combination) {
        return switch (combination) {
            case HighCard _ -> 5;
            case Pair _ -> 10 * 2;
            case TwoPair _ -> 20 * 2;
            case ThreeOfAKind _ -> 30 * 3;
            case Straight _ -> 30 * 4;
            case Flush _ -> 35 * 4;
            case FullHouse _ -> 40 * 4;
            case FourOfAKind _ -> 60 * 7;
            case StraightFlush _ -> 100 * 8;
        };
    }
}
