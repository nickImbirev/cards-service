package org.cards_tracker.service;

import org.cards_tracker.domain.CardPriorityUpdateSchedule;
import org.cards_tracker.error.IncorrectCardPriorityScheduleException;
import org.cards_tracker.error.NotExistingCardException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RunWith(MockitoJUnitRunner.class)
public class InMemoryCardsUpdateSchedulerTest {

    private InMemoryCardsUpdateScheduler cardsUpdateScheduler;

    @Mock
    private ScheduledExecutorService executorService;
    @Mock
    private CardRegistry cardRegistry;
    @Mock
    private PriorityUpdateCalendar priorityUpdateCalendar;

    // let's imagine that hours are days
    private final TimeUnit testTimeUnit = TimeUnit.HOURS;

    @Before
    public void setUp() throws IncorrectCardPriorityScheduleException {
        final Long defaultTestUpdateSchedulePeriod = 5L;
        cardsUpdateScheduler = new InMemoryCardsUpdateScheduler(
                executorService,
                priorityUpdateCalendar,
                cardRegistry,
                testTimeUnit, defaultTestUpdateSchedulePeriod
        );
        Mockito
                .when(executorService.schedule(Mockito.any(Runnable.class), Mockito.anyLong(), Mockito.any()))
                .thenReturn(null);
    }

    @Test
    public void shouldScheduleCardUpdateForTheClosestAvailableTime() throws IncorrectCardPriorityScheduleException {
        // arrange
        final String cardToSchedule = "card1";
        Mockito.when(cardRegistry.isCardExist(cardToSchedule)).thenReturn(true);
        final LocalDateTime testBeginning = LocalDateTime.of(2007, Month.AUGUST, 21, 12, 40, 10);
        final CardPriorityUpdateSchedule updateSchedule = new CardPriorityUpdateSchedule(testTimeUnit, 1L);
        final LocalDateTime expectedScheduledTime = LocalDateTime.of(2007, Month.AUGUST, 21, 13, 0);
        Mockito.when(priorityUpdateCalendar.nextPriorityUpdateFrom(updateSchedule, testBeginning)).thenReturn(expectedScheduledTime);
        // act
        try {
            cardsUpdateScheduler.scheduleCardPriorityUpdateFrom(testBeginning, cardToSchedule, updateSchedule);
        } catch (NotExistingCardException e) {
            Assert.fail(e.getMessage());
        }
        // assert
        final Set<String> actualScheduledCards = cardsUpdateScheduler.getActiveScheduleFor(expectedScheduledTime);

        Assert.assertTrue(actualScheduledCards.stream().anyMatch(cardName -> cardName.equals(cardToSchedule)));
    }

    @Test
    public void shouldNotScheduleNotExistingCardUpdate() throws IncorrectCardPriorityScheduleException {
        // arrange
        final String cardToSchedule = "card1";
        Mockito.when(cardRegistry.isCardExist(cardToSchedule)).thenReturn(false);
        // act
        try {
            cardsUpdateScheduler.scheduleCardPriorityUpdateFrom(
                    LocalDateTime.now(),
                    cardToSchedule,
                    new CardPriorityUpdateSchedule(TimeUnit.DAYS, 1L)
            );
            // assert
            Assert.fail("An error should be thrown.");
        } catch (NotExistingCardException e) {
            // do nothing
        }
    }

    @Test
    public void shouldScheduleTasksWithCommonScheduleForSameTime() throws Exception {
        // arrange
        Mockito.when(cardRegistry.isCardExist(Mockito.anyString())).thenReturn(true);

        final CardPriorityUpdateSchedule firstSchedule = new CardPriorityUpdateSchedule(testTimeUnit, 3L);
        final CardPriorityUpdateSchedule secondSchedule = new CardPriorityUpdateSchedule(firstSchedule.getTimeUnit(), 1L);

        final String firstCard = "card1";
        final LocalDateTime firstCardCreation = LocalDateTime.of(2007, Month.AUGUST, 21, 12, 30);
        final LocalDateTime expectedFirstCardTime = LocalDateTime.of(2007, Month.AUGUST, 21, 15, 0);
        Mockito.when(priorityUpdateCalendar.nextPriorityUpdateFrom(firstSchedule, firstCardCreation))
                .thenReturn(expectedFirstCardTime);
        cardsUpdateScheduler.scheduleCardPriorityUpdateFrom(firstCardCreation, firstCard, firstSchedule);

        final String secondCard = "card2";
        final LocalDateTime secondCardCreation = LocalDateTime.of(2007, Month.AUGUST, 21, 12, 40);
        final LocalDateTime secondCardFirstIteration = LocalDateTime.of(2007, Month.AUGUST, 21, 13, 0);
        Mockito.when(priorityUpdateCalendar.nextPriorityUpdateFrom(secondSchedule, secondCardCreation))
                .thenReturn(secondCardFirstIteration);
        final LocalDateTime secondCardSecondIteration = LocalDateTime.of(2007, Month.AUGUST, 21, 14, 0);
        Mockito.when(priorityUpdateCalendar.nextPriorityUpdateFrom(secondSchedule, secondCardFirstIteration))
                .thenReturn(secondCardSecondIteration);
        Mockito.when(priorityUpdateCalendar.nextPriorityUpdateFrom(secondSchedule, secondCardSecondIteration))
                .thenReturn(expectedFirstCardTime);
        // act
        cardsUpdateScheduler.scheduleCardPriorityUpdateFrom(secondCardCreation, secondCard, secondSchedule);

        cardsUpdateScheduler.updateCardsNewPriorityLevel(secondCardFirstIteration);
        cardsUpdateScheduler.updateCardsNewPriorityLevel(secondCardSecondIteration);
        // assert
        final Set<String> actualScheduledCards = cardsUpdateScheduler.getActiveScheduleFor(expectedFirstCardTime);
        Assert.assertEquals(2, actualScheduledCards.size());
    }

    @Test
    public void shouldScheduleTasksWithDifferentScheduleForDifferentTime() throws Exception {
        // arrange
        Mockito.when(cardRegistry.isCardExist(Mockito.anyString())).thenReturn(true);

        final CardPriorityUpdateSchedule firstSchedule = new CardPriorityUpdateSchedule(TimeUnit.DAYS, 1L);
        final CardPriorityUpdateSchedule secondSchedule = new CardPriorityUpdateSchedule(testTimeUnit, 8L);

        final String firstCard = "card1";
        final LocalDateTime firstCardCreation = LocalDateTime.of(2007, Month.AUGUST, 21, 12, 30);
        final LocalDateTime firstCardScheduledTime = LocalDateTime.of(2007, Month.AUGUST, 22, 0, 0);
        Mockito.when(priorityUpdateCalendar.nextPriorityUpdateFrom(firstSchedule, firstCardCreation))
                .thenReturn(firstCardScheduledTime);

        final String secondCard = "card2";
        final LocalDateTime secondCardCreation = LocalDateTime.of(2007, Month.AUGUST, 21, 10, 40);
        final LocalDateTime secondCardFirstIteration = LocalDateTime.of(2007, Month.AUGUST, 21, 18, 0);
        Mockito.when(priorityUpdateCalendar.nextPriorityUpdateFrom(secondSchedule, secondCardCreation))
                .thenReturn(secondCardFirstIteration);
        final LocalDateTime secondCardScheduledTime = LocalDateTime.of(2007, Month.AUGUST, 22, 2, 0);
        Mockito.when(priorityUpdateCalendar.nextPriorityUpdateFrom(secondSchedule, secondCardFirstIteration))
                .thenReturn(secondCardScheduledTime);
        // act
        cardsUpdateScheduler.scheduleCardPriorityUpdateFrom(firstCardCreation, firstCard, firstSchedule);

        cardsUpdateScheduler.scheduleCardPriorityUpdateFrom(secondCardCreation, secondCard, secondSchedule);
        cardsUpdateScheduler.updateCardsNewPriorityLevel(secondCardFirstIteration);
        // assert
        final Set<String> longRunningScheduledCards = cardsUpdateScheduler.getActiveScheduleFor(firstCardScheduledTime);
        Assert.assertEquals(1, longRunningScheduledCards.size());

        final Set<String> actualScheduledCards = cardsUpdateScheduler.getActiveScheduleFor(secondCardScheduledTime);
        Assert.assertEquals(1, actualScheduledCards.size());
    }
}
