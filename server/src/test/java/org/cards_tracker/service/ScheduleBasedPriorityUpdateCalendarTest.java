package org.cards_tracker.service;

import org.cards_tracker.domain.CardPriorityUpdateSchedule;
import org.cards_tracker.error.IncorrectCardPriorityScheduleException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.concurrent.TimeUnit;

public class ScheduleBasedPriorityUpdateCalendarTest {
    private ScheduleBasedPriorityUpdateCalendar priorityUpdateCalendar;

    @Before
    public void setUp() {
        priorityUpdateCalendar = new ScheduleBasedPriorityUpdateCalendar();
    }

    @Test
    public void shouldReturnNextUpdateTruncatedTillUsedTimeUnit() throws IncorrectCardPriorityScheduleException {
        // arrange
        final LocalDateTime from = LocalDateTime.of(2007, Month.AUGUST, 21, 12, 40, 10);
        CardPriorityUpdateSchedule schedule = new CardPriorityUpdateSchedule(TimeUnit.HOURS, 1L);
        // act
        final LocalDateTime nextUpdate = priorityUpdateCalendar.nextPriorityUpdateFrom(schedule, from);
        // assert
        final LocalDateTime expectedUpdateTime = LocalDateTime.of(2007, Month.AUGUST, 21, 13, 0);
        Assert.assertEquals(expectedUpdateTime, nextUpdate);
    }
}
