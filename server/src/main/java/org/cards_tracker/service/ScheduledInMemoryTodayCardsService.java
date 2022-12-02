package org.cards_tracker.service;

import org.cards_tracker.domain.Card;
import org.cards_tracker.error.CardAlreadyExistsException;
import org.cards_tracker.error.NotExistingCardException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.cards_tracker.util.Util.findFirstDistinct;
import static org.cards_tracker.util.Util.findFirstDuplicate;

@SuppressWarnings("unused")
public class ScheduledInMemoryTodayCardsService implements TodayCardsService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledInMemoryTodayCardsService.class);

    @NotNull
    private final CardRegistry cardRegistry;
    @NotNull
    private final Integer maxCardsForToday;
    @NotNull
    private List<String> cardsForToday = new ArrayList<>();

    public ScheduledInMemoryTodayCardsService(@NotNull final ScheduledExecutorService scheduledTaskExecutor,
                                              @NotNull final CardRegistry cardRegistry,
                                              @NotNull final TimeUnit timeUnit, @NotNull final Long period,
                                              @NotNull final Integer maxCardsForToday) {
        this.cardRegistry = cardRegistry;
        this.maxCardsForToday = maxCardsForToday;
        scheduledTaskExecutor.scheduleAtFixedRate(this::fillTheCardsForToday, 0, period, timeUnit);
        log.debug("Today cards preparation schedule: once per " + period + " " + timeUnit + " was configured.");
        log.debug("Today max cards number: " + maxCardsForToday + " was configured.");
    }

    void fillTheCardsForToday() {
        log.debug("List of cards for today with a limit of: " + maxCardsForToday +" started to being formed.");
        cardsForToday = cardRegistry.getPrioritizedCards().stream()
                .map(Card::getTitle)
                .limit(maxCardsForToday)
                .peek(title -> log.debug("Card: " + title + " was added for today."))
                .collect(Collectors.toList());
        log.debug("Cards for today were formed with " + cardsForToday.size() + " cards.");
    }

    @Override
    @NotNull
    public List<String> getCardsForToday() {
        return new ArrayList<>(cardsForToday);
    }

    @Override
    public void reshuffleTodayCards(@NotNull final List<String> orderedCards) throws NotExistingCardException, CardAlreadyExistsException {
        log.debug("Attempt to reshuffle today cards with a new order: " + String.join(", ", orderedCards) +  " has started.");
        final Optional<String> duplicate = findFirstDuplicate(orderedCards);
        if (duplicate.isPresent()) {
            final String cardTitle = duplicate.get();
            log.debug("Card with title: " + cardTitle + " is duplicated.");
            throw new CardAlreadyExistsException(cardTitle);
        }
        final Optional<String> distinct = findFirstDistinct(cardsForToday, orderedCards);
        if (distinct.isPresent()) {
            final String cardTitle = distinct.get();
            log.debug("Card with a title: " + cardTitle + " does not exist.");
            throw new NotExistingCardException(cardTitle);
        }
        this.cardsForToday = orderedCards;
        log.info("Today cards has been reshuffled with a new order: " + String.join(", ", cardsForToday) + ".");
    }

    @Override
    public void addAdditionalCardForToday(@NotNull final String title) throws NotExistingCardException {
        log.debug("Attempt to add an additional card with title: " + title + " for today started.");
        if (!cardRegistry.isCardExist(title)) {
            log.debug("Card with a title: " + title + " does not exist.");
            throw new NotExistingCardException(title);
        }
        if (cardsForToday.stream().noneMatch(card -> card.equals(title))) {
            cardsForToday.add(title);
            log.info("Card with title: " + title + " was added for today.");
        } else {
            log.debug("Card with title: " + title + " already exists in the today cards list.");
        }
    }

    @Override
    public void completeCardForToday(@NotNull final String title) throws NotExistingCardException {
        log.debug("Attempt to complete today card with title: " + title + " started.");
        if (!cardsForToday.removeIf((dayCardTitle) -> dayCardTitle.equals(title))) {
            log.debug("Card with a title: " + title + " does not exist in the today cards list.");
            throw new NotExistingCardException(title);
        }
        try {
            cardRegistry.bottomCardPriority(title);
        } catch (NotExistingCardException e) {
            log.debug("Card with a title: " + title + " does not exist in the registry.");
        }
        log.info("Today card with title: " + title + " was completed.");
    }
}
