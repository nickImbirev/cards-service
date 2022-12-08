package org.cards_tracker.service;

import org.cards_tracker.domain.CardPriorityUpdateSchedule;
import org.cards_tracker.error.IncorrectCardPriorityScheduleException;
import org.cards_tracker.error.NotExistingCardException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class InMemoryCardsUpdateScheduler implements CardsUpdateScheduler {

    private static final Logger log = LoggerFactory.getLogger(InMemoryCardsUpdateScheduler.class);

    @NotNull
    private final CardRegistry cardRegistry;
    @NotNull
    private final CardPriorityUpdateSchedule defaultPriorityUpdateSchedule;
    @NotNull
    private final ScheduledExecutorService scheduledTasksExecutor;
    @NotNull
    private final PriorityUpdateCalendar priorityUpdateCalendar;
    @NotNull
    private final Map<LocalDateTime, ScheduledDetails> scheduledCards = new HashMap<>();

    static class ScheduledDetails {
        @NotNull
        private final CardPriorityUpdateSchedule schedule;
        @NotNull
        private final Set<String> scheduledCards;

        public ScheduledDetails(@NotNull final CardPriorityUpdateSchedule schedule) {
            this.schedule = schedule;
            this.scheduledCards = new HashSet<>();
        }

        void addCard(@NotNull final String title) {
            scheduledCards.add(title);
        }
    }

    public InMemoryCardsUpdateScheduler(@NotNull final ScheduledExecutorService scheduledTasksExecutor,
                                        @NotNull final PriorityUpdateCalendar priorityUpdateCalendar,
                                        @NotNull final CardRegistry cardRegistry,
                                        @NotNull final TimeUnit applicationDayTimeUnit,
                                        @NotNull final Long updateSchedulePeriod) throws IncorrectCardPriorityScheduleException {
        this.scheduledTasksExecutor = scheduledTasksExecutor;
        this.cardRegistry = cardRegistry;
        this.priorityUpdateCalendar = priorityUpdateCalendar;
        this.defaultPriorityUpdateSchedule = new CardPriorityUpdateSchedule(applicationDayTimeUnit, updateSchedulePeriod);
        log.debug("Default card priority update schedule: " + defaultPriorityUpdateSchedule + " was configured.");
    }

    @Override
    public void scheduleDefaultPriorityUpdate(@NotNull final String title) throws NotExistingCardException {
        scheduleCardPriorityUpdateFrom(LocalDateTime.now(), title, this.defaultPriorityUpdateSchedule);
    }

    @Override
    public void schedulePriorityUpdate(@NotNull final String title,
                                       @NotNull final CardPriorityUpdateSchedule priorityUpdateSchedule)
            throws NotExistingCardException {
        scheduleCardPriorityUpdateFrom(LocalDateTime.now(), title, priorityUpdateSchedule);
    }

    void scheduleCardPriorityUpdateFrom(@NotNull final LocalDateTime from,
                                        @NotNull final String title,
                                        @NotNull final CardPriorityUpdateSchedule priorityUpdateSchedule)
            throws NotExistingCardException {
        log.debug("Attempt to schedule a card: " + title + " priority update started at: " + from + ".");
        if (!cardRegistry.isCardExist(title)) {
            log.debug("Card: " + title + " was not found to schedule a priority update.");
            throw new NotExistingCardException(title);
        }
        final LocalDateTime nextPriorityUpdate = priorityUpdateCalendar.nextPriorityUpdateFrom(priorityUpdateSchedule, from);
        if (scheduledCards.containsKey(nextPriorityUpdate)) {
            scheduledCards.get(nextPriorityUpdate).addCard(title);
            log.debug("New card with title: " + title + " was added to scheduled execution at: " + nextPriorityUpdate + ".");
        } else {
            final ScheduledDetails scheduledDetails = new ScheduledDetails(priorityUpdateSchedule);
            scheduledDetails.addCard(title);
            scheduledCards.put(nextPriorityUpdate, scheduledDetails);
            scheduledTasksExecutor.schedule(
                    () -> updateCardsNewPriorityLevel(nextPriorityUpdate),
                    from.until(nextPriorityUpdate, ChronoUnit.MILLIS), TimeUnit.MILLISECONDS
            );
            log.debug("New card with title: " + title + " was scheduled to execute at: " + nextPriorityUpdate + ".");
        }
        log.info("Card: " + title + " priority update was scheduled.");
    }

    void updateCardsNewPriorityLevel(@NotNull final LocalDateTime from) {
        log.debug("Cards sync started at: " + from + ".");
        ScheduledDetails scheduledDetails = scheduledCards.get(from);
        if (scheduledDetails == null) {
            log.debug("No scheduled cards found for the time: " + from + ".");
            return;
        }
        final Set<String> cards = scheduledDetails.scheduledCards;
        log.debug("Cards sync started at: " + from + " for cards: " + String.join(",", cards) + ".");
        cards.forEach((title) -> {
            try {
                cardRegistry.increaseCardPriority(title);
            } catch (NotExistingCardException e) {
                log.debug("Unable to update card " + title + " priority, because it was not found.");
                return;
            }
            log.debug("Card: " + title + " priority was updated.");
            final LocalDateTime nextPriorityUpdate = priorityUpdateCalendar.nextPriorityUpdateFrom(scheduledDetails.schedule, from);
            if (scheduledCards.containsKey(nextPriorityUpdate)) {
                scheduledCards.get(nextPriorityUpdate).addCard(title);
                log.debug("Card with title: " + title + " was added to scheduled execution at: " + nextPriorityUpdate + ".");
            } else {
                final ScheduledDetails nextTimeSchedule = new ScheduledDetails(scheduledDetails.schedule);
                nextTimeSchedule.addCard(title);
                scheduledCards.put(nextPriorityUpdate, nextTimeSchedule);
                scheduledTasksExecutor.schedule(
                        () -> updateCardsNewPriorityLevel(nextPriorityUpdate),
                        from.until(nextPriorityUpdate, ChronoUnit.MILLIS), TimeUnit.MILLISECONDS
                );
                log.debug("Card with title: " + title + " was scheduled to execute at: " + nextPriorityUpdate + ".");
            }
        });
        log.info("Cards sync started at: " + from + " was completed.");
    }

    @NotNull
    Set<String> getActiveScheduleFor(@NotNull final LocalDateTime scheduleTime) {
        final ScheduledDetails scheduledDetails = scheduledCards.get(scheduleTime);
        if (scheduledDetails == null) return new HashSet<>();
        return scheduledDetails.scheduledCards;
    }
}
