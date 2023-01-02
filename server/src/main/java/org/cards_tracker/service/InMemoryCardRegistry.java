package org.cards_tracker.service;

import org.cards_tracker.domain.Card;
import org.cards_tracker.domain.CardPriority;
import org.cards_tracker.error.CardAlreadyExistsException;
import org.cards_tracker.error.IncorrectCardTitleException;
import org.cards_tracker.error.NotExistingCardException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.cards_tracker.domain.CardPriority.*;

@SuppressWarnings("unused")
public class InMemoryCardRegistry implements CardRegistry {

    private static final Logger log = LoggerFactory.getLogger(InMemoryCardRegistry.class);

    private static final List<CardPriority> PRIORITIES_ORDER = List.of(
            I_NEED_TO_DO_IT_TODAY_OR_TOMORROW,
            WOW_I_NEEDED_TO_DO_IT_YESTERDAY,
            LEAVE_EVERYTHING_AND_START_WORKING_ON_IT
    );

    @NotNull
    private static CardPriority nextPriorityLevel(@NotNull final CardPriority currentPriority) {
        int nextPriorityIndex = PRIORITIES_ORDER.indexOf(currentPriority) + 1;
        if (nextPriorityIndex == PRIORITIES_ORDER.size()) {
            log.debug("Card max possible priority has been achieved.");
            return currentPriority;
        }
        return PRIORITIES_ORDER.get(nextPriorityIndex);
    }

    @NotNull
    private final Map<String, Card> allCards = new ConcurrentHashMap<>();

    @Override
    public void createCard(@NotNull final Card card) throws CardAlreadyExistsException {
        final String title = card.getTitle();
        log.debug("Attempt to create a new card with title: " + title + " started.");
        if (allCards.get(title) != null) {
            log.debug("Card with title: " + title + " already exists.");
            throw new CardAlreadyExistsException(title);
        }
        allCards.put(title, card);
        log.info("New card created with title: " + title + ".");
    }

    @Override
    public boolean isCardExist(@NotNull final String title) {
        return allCards.get(title) != null;
    }

    @Override
    @NotNull
    public Card getCard(@NotNull final String title) throws NotExistingCardException {
        final Card existingCard = allCards.get(title);
        if (existingCard == null) {
            throw new NotExistingCardException(title);
        }
        return existingCard;
    }

    @Override
    @NotNull
    public Set<Card> getAllCards() {
        return new HashSet<>(allCards.values());
    }

    @NotNull
    @Override
    public CardPriority getInitialCardPriority() {
        return PRIORITIES_ORDER.get(0);
    }

    @Override
    public @NotNull CardPriority getMaxCardPriority() {
        return PRIORITIES_ORDER.get(PRIORITIES_ORDER.size() - 1);
    }

    @Override
    @NotNull
    public List<Card> getPrioritizedCards() {
        return allCards.values().stream()
                .sorted((firstEntry, secondEntry) -> {
                    int compareResult =
                            PRIORITIES_ORDER.indexOf(secondEntry.getCardPriority()) -
                                    PRIORITIES_ORDER.indexOf(firstEntry.getCardPriority());
                    if (compareResult == 0) {
                        log.debug("Card: " + firstEntry.getTitle() + " and card: " + secondEntry.getTitle() + " has equal priorities.");
                    } else if (compareResult > 0) {
                        log.debug("Card: " + firstEntry.getTitle() + " is more prioritized than card: " + secondEntry.getTitle() + ".");
                    } else {
                        log.debug("Card: " + secondEntry.getTitle() + " is more prioritized than card: " + firstEntry.getTitle() + ".");
                    }
                    return compareResult;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void updateCard(@NotNull final Card updatedCard) throws NotExistingCardException {
        final String title = updatedCard.getTitle();
        log.debug("Attempt to update a card with title: " + title + " started.");
        if (allCards.get(title) == null) {
            log.debug("Card with title: " + title + " was not found.");
            throw new NotExistingCardException(title);
        }
        allCards.replace(title, updatedCard);
        log.info("Card updated with title: " + title + ".");
    }

    @Override
    public void updateCardPriority(@NotNull String title, @NotNull CardPriority updatedPriority) throws NotExistingCardException {
        log.debug("Attempt to update a card with title: " + title + " started.");
        if (allCards.get(title) == null) {
            log.debug("Card with title: " + title + " was not found.");
            throw new NotExistingCardException(title);
        }
        final Card cardToUpdate;
        try {
            cardToUpdate = new Card(title, updatedPriority);
        } catch (IncorrectCardTitleException e) {
            log.warn("Card with incorrect title: " + title + ", exists.");
            // impossible use case, unless the developer's mistake
            return;
        }
        allCards.replace(title, cardToUpdate);
        log.info("Card updated with title: " + title + ".");
    }


    @Override
    public void bottomCardPriority(@NotNull String title) throws NotExistingCardException {
        log.debug("Attempt to bottom card priority with title: " + title + " started.");
        final CardPriority initialPriority = getInitialCardPriority();
        updateCardPriority(title, initialPriority);
        log.info("Card with title: " + title + " priority was bottomed to priority: " + initialPriority + ".");
    }

    @Override
    public void increaseCardPriority(@NotNull String title) throws NotExistingCardException {
        log.debug("Attempt to increase card priority with title: " + title + " started.");
        final Card existingCard = allCards.get(title);
        if (existingCard == null) {
            log.debug("Card with title: " + title + " was not found.");
            throw new NotExistingCardException(title);
        }
        final CardPriority nextPriority = nextPriorityLevel(existingCard.getCardPriority());
        updateCardPriority(title, nextPriority);
        log.info("Card with title: " + title + " priority was increased to priority: " + nextPriority + ".");
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
}
