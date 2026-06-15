package fr.uge.azathro.domain;

import fr.uge.azathro.domain.types.combination.*;
import fr.uge.azathro.domain.types.planet.Planet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public interface Dealer {
    record HandAnalysis(int baseChips, boolean isFlush, boolean isStraight,
                        boolean hasFour, boolean hasThree, long pairCount) {
    }

    record CombinationRule(Predicate<HandAnalysis> condition, Planet planet,
                           int baseChipsBonus, int baseMult,
                           BiFunction<Integer, Integer, Combination> factory) {
    }

    List<CombinationRule> COMBINATION_RULES = List.of(
            new CombinationRule(a -> a.isStraight() && a.isFlush(), Planet.NEPTUNE, 100, 8, StraightFlush::new),
            new CombinationRule(HandAnalysis::hasFour, Planet.MARS, 60, 7, FourOfAKind::new),
            new CombinationRule(a -> a.hasThree() && a.pairCount() == 1, Planet.TERRE, 40, 4, FullHouse::new),
            new CombinationRule(HandAnalysis::isFlush, Planet.JUPITER, 35, 4, Flush::new),
            new CombinationRule(HandAnalysis::isStraight, Planet.SATURNE, 30, 4, Straight::new),
            new CombinationRule(HandAnalysis::hasThree, Planet.VENUS, 30, 3, ThreeOfAKind::new),
            new CombinationRule(a -> a.pairCount() == 2, Planet.URANUS, 20, 2, TwoPair::new),
            new CombinationRule(a -> a.pairCount() == 1, Planet.MERCURE, 10, 2, Pair::new),
            new CombinationRule(_ -> true, Planet.PLUTON, 5, 1, HighCard::new)
    );

    static Combination evaluate(Hand hand, Map<Planet, Integer> planets) {
        Objects.requireNonNull(hand);
        Objects.requireNonNull(planets);
        var cards = hand.cards();
        if (cards.isEmpty() || cards.size() > 5) {
            throw new IllegalArgumentException("cards size must be between 1 and 5");
        }
        var handAnalysis = analyzeHand(cards);
        return selectCombination(handAnalysis, planets);
    }

    private static HandAnalysis analyzeHand(List<Card> cards) {
        var baseChips = cards.stream().mapToInt(card -> card.rank().value()).sum();
        var sortedValues = cards.stream().map(card -> card.rank().value()).sorted().toList();
        var suits = cards.stream().map(Card::suit).toList();
        var countsByValue = countByValue(sortedValues);
        var isFlush = cards.size() == 5 && suits.stream().distinct().count() == 1;
        var isStraight = cards.size() == 5 && isStraight(sortedValues);
        var hasFour = countsByValue.containsValue(4);
        var hasThree = countsByValue.containsValue(3);
        var pairCount = countsByValue.values().stream().filter(count -> count == 2).count();
        return new HandAnalysis(baseChips, isFlush, isStraight, hasFour, hasThree, pairCount);
    }

    private static Combination selectCombination(HandAnalysis analysis, Map<Planet, Integer> planets) {
        return COMBINATION_RULES.stream()
                .filter(rule -> rule.condition().test(analysis))
                .findFirst()
                .map(rule -> rule.factory().apply(
                        analysis.baseChips() + rule.baseChipsBonus() + bonusChips(rule.planet(), planets),
                        rule.baseMult() + bonusMult(rule.planet(), planets)))
                .orElseThrow();
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
