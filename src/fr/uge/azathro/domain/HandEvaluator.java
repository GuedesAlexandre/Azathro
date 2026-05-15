package fr.uge.azathro.domain;

import fr.uge.azathro.domain.types.combination.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

public record HandEvaluator() {
    public static Combination evaluate(Hand hand) {
        Objects.requireNonNull(hand);
        var cards = hand.cards();
        if (cards.size() < 1 || cards.size() > 5) {
            throw new IllegalArgumentException("cards size must be between 1 and 5");
        }
        var values = cards.stream().map(c -> c.rank().value()).sorted().toList();
        var suits = cards.stream().map(Card::suit).toList();
        var counts = countByValue(values);
        var isFlush = false;
        var isStraight = false;
        if (cards.size() == 5) {
        	isFlush = suits.stream().distinct().count() == 1;
            isStraight = isStraight(values);
        }
        var hasFour = counts.containsValue(4);
        var hasThree = counts.containsValue(3);
        var pairs = counts.values().stream().filter(v -> v == 2).count();
        if (isStraight && isFlush) return new StraightFlush();
        if (hasFour) return new FourOfAKind();
        if (hasThree && pairs == 1) return new FullHouse();
        if (isFlush) return new Flush();
        if (isStraight) return new Straight();
        if (hasThree) return new ThreeOfAKind();
        if (pairs == 2) return new TwoPair();
        if (pairs == 1) return new Pair();
        return new HighCard();
    }

    private static boolean isStraight(List<Integer> values) {
        if (values.size() != 5) {
            return false;
        }
        List<Integer> sorted = values.stream().sorted().toList();
        var isNormalStraight = IntStream.range(0, 4)
                .allMatch(i -> sorted.get(i + 1) - sorted.get(i) == 1);

        var isWheel = sorted.equals(List.of(2, 3, 4, 5, 14));

        return isNormalStraight || isWheel;
    }

    private static Map<Integer, Integer> countByValue(List<Integer> values) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int value : values) {
            counts.merge(value, 1, Integer::sum);
        }
        return counts;
    }


}
