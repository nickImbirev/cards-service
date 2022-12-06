package org.cards_tracker.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cards_tracker.error.IncorrectCardPriorityScheduleException;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class PriorityUpdateSchedule {
    @NotNull
    private final CardPriorityTimeUnit timeUnit;
    @NotNull
    private final Long period;

    @JsonCreator
    public PriorityUpdateSchedule(
            @JsonProperty(required = true, value = "timeUnit") @NotNull(exception = IncorrectCardPriorityScheduleException.class) final CardPriorityTimeUnit timeUnit,
            @JsonProperty(required = true, value = "period") @NotNull(exception = IncorrectCardPriorityScheduleException.class) final Long period)
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
    public CardPriorityTimeUnit getTimeUnit() {
        return timeUnit;
    }

    @NotNull
    @JsonIgnore
    public TimeUnit getSdkTimeUnit() throws IllegalArgumentException {
        return TimeUnit.valueOf(this.timeUnit.toString());
    }

    @NotNull
    public Long getPeriod() {
        return period;
    }

    @Override
    public String toString() {
        return "CardPriorityUpdateSchedule{" +
                "timeUnit=" + timeUnit +
                ", period=" + period +
                '}';
    }
}
