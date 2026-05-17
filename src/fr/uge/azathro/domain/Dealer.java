package fr.uge.azathro.domain;

import fr.uge.azathro.domain.types.combination.*;
import fr.uge.azathro.domain.types.planet.Planet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

public record Dealer() {
    public static Combination evaluate(Hand hand, Map<Planet, Integer> planets) {
        Objects.requireNonNull(hand);
        Objects.requireNonNull(planets);
        var cards = hand.cards();
        if (cards.isEmpty() || cards.size() > 5) {
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
        if (isStraight && isFlush) {
            return new StraightFlush(
                    100 + bonusChips(Planet.NEPTUNE, planets),
                    8 + bonusMult(Planet.NEPTUNE, planets)
            );
        }
        if (hasFour) {
            return new FourOfAKind(
                    60 + bonusChips(Planet.MARS, planets),
                    7 + bonusMult(Planet.MARS, planets)
            );
        }
        if (hasThree && pairs == 1) {
            return new FullHouse(
                    40 + bonusChips(Planet.TERRE, planets),
                    4 + bonusMult(Planet.TERRE, planets)
            );
        }
        if (isFlush) {
            return new Flush(
                    35 + bonusChips(Planet.JUPITER, planets),
                    4 + bonusMult(Planet.JUPITER, planets)
            );
        }
        if (isStraight) {
            return new Straight(
                    30 + bonusChips(Planet.SATURNE, planets),
                    4 + bonusMult(Planet.SATURNE, planets)
            );
        }
        if (hasThree) {
            return new ThreeOfAKind(
                    30 + bonusChips(Planet.VENUS, planets),
                    3 + bonusMult(Planet.VENUS, planets)
            );
        }
        if (pairs == 2) {
            return new TwoPair(
                    20 + bonusChips(Planet.URANUS, planets),
                    2 + bonusMult(Planet.URANUS, planets)
            );
        }
        if (pairs == 1) {
            return new Pair(
                    10 + bonusChips(Planet.MERCURE, planets),
                    2 + bonusMult(Planet.MERCURE, planets)
            );
        }
        return new HighCard(
                5 + bonusChips(Planet.PLUTON, planets),
                1 + bonusMult(Planet.PLUTON, planets)
        );
    }

    private static int bonusChips(Planet planet, Map<Planet, Integer> planets) {
        return planet.bonusChips() * planets.getOrDefault(planet, 0);
    }

    private static int bonusMult(Planet planet, Map<Planet, Integer> planets) {
        return planet.bonusMult() * planets.getOrDefault(planet, 0);
    }

    private static boolean isStraight(List<Integer> values) {
        if (values.size() != 5) {
            return false;
        }
        var sorted = values.stream().sorted().toList();
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
