package fr.uge.azathro.view;

import fr.uge.azathro.domain.Blind;
import fr.uge.azathro.domain.Card;
import fr.uge.azathro.model.PlayerState;

import java.util.List;
import java.util.Set;

public record GraphicViewState(
        List<Card> currentHand,
        Blind currentBlind,
        int deckSize,
        int discardSize,
        PlayerState playerState,
        Set<Integer> selectedIndexes,
        String lastCombination,
        Integer lastScore
) {}
