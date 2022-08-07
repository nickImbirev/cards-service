package org.cards_tracker.service;

import org.cards_tracker.domain.CardPriority;
import org.cards_tracker.error.CardAlreadyExistsException;
import org.cards_tracker.error.IncorrectCardTitleException;
import org.cards_tracker.error.NotExistingCardException;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.cards_tracker.domain.CardPriority.*;

@SuppressWarnings("unused")
public class ScheduledInMemoryCardService implements CardService {

    private final Map<String, CardPriority> allCards = new HashMap<>();

    private static final List<CardPriority> PRIORITIES_ORDER = List.of(
            I_NEED_TO_DO_IT_TODAY_OR_TOMORROW,
            WOW_I_NEEDED_TO_DO_IT_YESTERDAY,
            LEAVE_EVERYTHING_AND_START_WORKING_ON_IT
    );

    @NotNull
    private List<String> cardsForToday = new ArrayList<>();
    @NotNull
    private final Integer maxCardsForToday;

    public ScheduledInMemoryCardService(
            @NotNull final TimeUnit timeUnit, @NotNull final Long period,
            @NotNull final Integer maxCardsForToday) {
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this::updateTodayCards, 0, period, timeUnit);
        this.maxCardsForToday = maxCardsForToday;
    }

    void updateTodayCards() {
        updateCardsNewPriorityLevel();
        fillTheCardsForToday();
    }

    private void updateCardsNewPriorityLevel() {
        allCards.replaceAll((title, priority) -> nextPriorityLevel(priority));
    }

    @NotNull
    private CardPriority nextPriorityLevel(@NotNull final CardPriority currentPriority) {
        int nextPriorityIndex = PRIORITIES_ORDER.indexOf(currentPriority) + 1;
        if (nextPriorityIndex == PRIORITIES_ORDER.size()) {
            return currentPriority;
        }
        return PRIORITIES_ORDER.get(nextPriorityIndex);
    }

    private void fillTheCardsForToday() {
        cardsForToday = allCards.entrySet().stream()
                .sorted((firstEntry, secondEntry) ->
                        PRIORITIES_ORDER.indexOf(secondEntry.getValue()) - PRIORITIES_ORDER.indexOf(firstEntry.getValue()))
                .limit(maxCardsForToday)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public void createCard(@NotNull String title) throws IncorrectCardTitleException, CardAlreadyExistsException {
        if (allCards.get(title) != null) throw new CardAlreadyExistsException(title);
        if (isTitleInvalid(title)) throw new IncorrectCardTitleException(title);
        allCards.put(title, getInitialPriority());
    }

    private boolean isTitleInvalid(@NotNull String title) {
        return title.trim().length() == 0;
    }

    @NotNull
    private CardPriority getInitialPriority() {
        return PRIORITIES_ORDER.get(0);
    }

    @Override
    @NotNull
    public List<String> getCardsForToday() {
        return new ArrayList<>(cardsForToday);
    }

    @Override
    public void addAdditionalCardForToday(@NotNull final String title) throws NotExistingCardException {
        if (allCards.get(title) == null) {
            throw new NotExistingCardException(title);
        }
        if (cardsForToday.stream().noneMatch(card -> card.equals(title))) {
            cardsForToday.add(title);
        }
    }

    @Override
    public void completeCardForToday(@NotNull String title) throws NotExistingCardException {
        if (!cardsForToday.removeIf((dayCardTitle) -> dayCardTitle.equals(title))) {
            throw new NotExistingCardException(title);
        }
        allCards.replace(title, getInitialPriority());
    }

    @Override
    public void removeCard(@NotNull String title) {
        if (allCards.get(title) == null) return;
        cardsForToday.removeIf((dayCardTitle) -> dayCardTitle.equals(title));
        allCards.remove(title);
    }
}
