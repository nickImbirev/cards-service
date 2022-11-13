package org.cards_tracker.service;

import org.cards_tracker.domain.CardPriority;
import org.cards_tracker.error.CardAlreadyExistsException;
import org.cards_tracker.error.IncorrectCardTitleException;
import org.cards_tracker.error.NotExistingCardException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.cards_tracker.domain.CardPriority.*;

@SuppressWarnings("unused")
public class ScheduledInMemoryCardRegistry implements CardRegistry {

    private static final Logger log = LoggerFactory.getLogger(ScheduledInMemoryCardRegistry.class);

    @NotNull
    private final Map<String, CardPriority> allCards = new HashMap<>();
    @NotNull
    private final ScheduledFuture<?> scheduledTask;

    private static final List<CardPriority> PRIORITIES_ORDER = List.of(
            I_NEED_TO_DO_IT_TODAY_OR_TOMORROW,
            WOW_I_NEEDED_TO_DO_IT_YESTERDAY,
            LEAVE_EVERYTHING_AND_START_WORKING_ON_IT
    );

    public ScheduledInMemoryCardRegistry(
            @NotNull final TimeUnit timeUnit, @NotNull final Long period) {
        this.scheduledTask = Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this::updateCardsPriority, 0, period, timeUnit);
        log.debug("Cards sync schedule: once per " + period + " " + timeUnit + " was configured.");
    }

    void terminate() {
        try {
            this.scheduledTask.cancel(false);
        } catch (Exception e) {
            // do nothing
        }
    }

    void updateCardsPriority() {
        log.info("Cards sync started.");
        updateCardsNewPriorityLevel();
        log.debug("Cards priority was updated.");
        log.info("Cards sync ended.");
    }

    private void updateCardsNewPriorityLevel() {
        allCards.replaceAll((title, priority) -> {
            log.debug("Card's " + title + " priority: " + priority + ".");
            final CardPriority nextPriority = nextPriorityLevel(priority);
            log.debug("Card's" + title + " new priority: " + nextPriority + ".");
            return nextPriority;
        });
    }

    @NotNull
    private CardPriority nextPriorityLevel(@NotNull final CardPriority currentPriority) {
        int nextPriorityIndex = PRIORITIES_ORDER.indexOf(currentPriority) + 1;
        if (nextPriorityIndex == PRIORITIES_ORDER.size()) {
            log.debug("Card max possible priority has been achieved.");
            return currentPriority;
        }
        return PRIORITIES_ORDER.get(nextPriorityIndex);
    }

    @Override
    public void createCard(@NotNull final String title) throws IncorrectCardTitleException, CardAlreadyExistsException {
        final CardPriority initialPriority = getInitialPriority();
        createCard(title, initialPriority);
    }

    void createCard(@NotNull final String title, @NotNull final CardPriority cardPriority) throws IncorrectCardTitleException, CardAlreadyExistsException {
        log.debug("Attempt to create a new card with title: " + title + " started.");
        if (isTitleInvalid(title)) {
            log.debug("Card title: " + title + " is incorrect.");
            throw new IncorrectCardTitleException(title);
        }
        if (allCards.get(title) != null) {
            log.debug("Card with title: " + title + " already exists.");
            throw new CardAlreadyExistsException(title);
        }
        allCards.put(title, cardPriority);
        log.info("New card with title: " + title + " created with priority: " + cardPriority + ".");
    }

    private boolean isTitleInvalid(@NotNull final String title) {
        return title.trim().length() == 0;
    }

    @NotNull
    private CardPriority getInitialPriority() {
        return PRIORITIES_ORDER.get(0);
    }

    @Override
    public void removeCard(@NotNull final String title) {
        log.debug("Attempt to remove card with title: " + title + " started.");
        if (allCards.get(title) == null) {
            log.debug("Card with title: " + title + " was not found and does need to be removed.");
            return;
        }
        if (allCards.remove(title) != null) {
            log.debug("Card with title: " + title + " was removed from the global storage.");
        }
        log.info("Card with title: " + title + " was removed.");
    }

    @Override
    public void bottomCardPriority(@NotNull String title) throws NotExistingCardException {
        log.debug("Attempt to bottom card priority with title: " + title + " started.");
        if (allCards.get(title) == null) {
            log.debug("Card with title: " + title + " was not found.");
            throw new NotExistingCardException(title);
        }
        final CardPriority initialPriority = getInitialPriority();
        allCards.replace(title, initialPriority);
        log.info("Card with title: " + title + " priority was bottomed to priority: " + initialPriority + ".");
    }

    @Override
    public boolean isCardExist(@NotNull String title) {
        return allCards.get(title) != null;
    }

    @Override
    @NotNull
    public List<String> getPrioritizedCards() {
        return allCards.entrySet().stream()
                .sorted((firstEntry, secondEntry) -> {
                    int compareResult = PRIORITIES_ORDER.indexOf(secondEntry.getValue()) - PRIORITIES_ORDER.indexOf(firstEntry.getValue());
                    if (compareResult == 0) {
                        log.debug("Card: " + firstEntry.getKey() + " and card: " + secondEntry.getKey() + " has equal priorities.");
                    } else if (compareResult > 0) {
                        log.debug("Card: " + firstEntry.getKey() + " is more prioritized than card: " + secondEntry.getKey() + ".");
                    } else {
                        log.debug("Card: " + secondEntry.getKey() + " is more prioritized than card: " + firstEntry.getKey() + ".");
                    }
                    return compareResult;
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
