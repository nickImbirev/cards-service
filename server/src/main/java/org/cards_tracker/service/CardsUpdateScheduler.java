package org.cards_tracker.service;

import org.cards_tracker.domain.CardPriorityUpdateSchedule;
import org.cards_tracker.error.NotExistingCardException;
import org.jetbrains.annotations.NotNull;

public interface CardsUpdateScheduler {

    void scheduleDefaultPriorityUpdate(@NotNull final String title) throws NotExistingCardException;

    void schedulePriorityUpdate(@NotNull final String title,
                                @NotNull final CardPriorityUpdateSchedule priorityUpdateSchedule)
            throws NotExistingCardException;
}
