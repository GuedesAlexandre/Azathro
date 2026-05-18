package fr.uge.azathro.domain.types.combination;

public sealed interface Combination permits Flush, FourOfAKind, FullHouse, HighCard, Pair, Straight, StraightFlush, ThreeOfAKind, TwoPair {
    static int computeScore(Combination combination) {
        return switch (combination) {
            case HighCard h -> h.chips() * h.multiplier();
            case Pair p -> p.chips() * p.multiplier();
            case TwoPair tp -> tp.chips() * tp.multiplier();
            case ThreeOfAKind t -> t.chips() * t.multiplier();
            case Straight s -> s.chips() * s.multiplier();
            case Flush f -> f.chips() * f.multiplier();
            case FullHouse fh -> fh.chips() * fh.multiplier();
            case FourOfAKind fk -> fk.chips() * fk.multiplier();
            case StraightFlush sf -> sf.chips() * sf.multiplier();
        };
    }
}
