package org.cards_tracker.service;

import org.cards_tracker.domain.CardPriorityUpdateSchedule;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

public interface PriorityUpdateCalendar {
    @NotNull
    LocalDateTime nextPriorityUpdateFrom(@NotNull final CardPriorityUpdateSchedule schedule, @NotNull final LocalDateTime from);
}
