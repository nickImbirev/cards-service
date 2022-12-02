package org.cards_tracker.domain;

import org.cards_tracker.error.IncorrectCardPriorityScheduleException;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class CardPriorityUpdateSchedule {
    @NotNull
    private final TimeUnit timeUnit;
    @NotNull
    private final Long period;

    public CardPriorityUpdateSchedule(@NotNull final TimeUnit timeUnit, @NotNull final Long period)
            throws IncorrectCardPriorityScheduleException {
        if (!isPeriodValid(period)) {
            throw new IncorrectCardPriorityScheduleException("Card priority update schedule period is incorrect");
        }
        this.timeUnit = timeUnit;
        this.period = period;
    }

    private boolean isPeriodValid(@NotNull final Long period) {
        return period > 0;
    }

    @NotNull
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    @NotNull
    public Long getPeriod() {
        return period;
    }

    @Override
    @NotNull
    public String toString() {
        return "CardPriorityUpdateSchedule{" +
                "timeUnit=" + timeUnit +
                ", period=" + period +
                '}';
    }
}
