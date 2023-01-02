package org.cards_tracker.service;

import org.cards_tracker.domain.CardPriorityUpdateSchedule;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ScheduleBasedPriorityUpdateCalendar implements PriorityUpdateCalendar {
    @Override
    @NotNull
    public LocalDateTime nextPriorityUpdateFrom(@NotNull final CardPriorityUpdateSchedule schedule,
                                                @NotNull final LocalDateTime from) {
        switch (schedule.getTimeUnit()) {
            case MINUTES:
                return from.plus(schedule.getPeriod(), ChronoUnit.MINUTES).truncatedTo(ChronoUnit.MINUTES);
            case DAYS:
                return from.plus(schedule.getPeriod(), ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
            case HOURS:
                return from.plus(schedule.getPeriod(), ChronoUnit.HOURS).truncatedTo(ChronoUnit.HOURS);
            default:
                return from.plus(schedule.getPeriod(), ChronoUnit.SECONDS).truncatedTo(ChronoUnit.SECONDS);
        }
    }
}
