package org.cards_tracker.error;

import org.jetbrains.annotations.NotNull;

public class IncorrectCardPriorityScheduleException extends Exception {
    public IncorrectCardPriorityScheduleException(@NotNull final String reason) {
        super(reason);
    }
}
