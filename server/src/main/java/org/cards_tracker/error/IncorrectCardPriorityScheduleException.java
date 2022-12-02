package org.cards_tracker.error;

import org.cards_tracker.domain.CardPriorityUpdateSchedule;
import org.jetbrains.annotations.NotNull;

public class IncorrectCardPriorityScheduleException extends Exception {
    public IncorrectCardPriorityScheduleException(@NotNull final CardPriorityUpdateSchedule priorityUpdateSchedule) {
        super("The card priority update schedule: " + priorityUpdateSchedule + " is incorrect");
    }

    public IncorrectCardPriorityScheduleException(@NotNull final String reason) {
        super(reason);
    }
}
