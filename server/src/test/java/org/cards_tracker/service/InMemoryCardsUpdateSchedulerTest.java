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

    // let's imagine that hours are days
    private final TimeUnit testTimeUnit = TimeUnit.HOURS;

    @Before
    public void setUp() throws IncorrectCardPriorityScheduleException {
        final Long defaultTestUpdateSchedulePeriod = 5L;
        cardsUpdateScheduler = new InMemoryCardsUpdateScheduler(
                executorService,
                cardRegistry,
                testTimeUnit, defaultTestUpdateSchedulePeriod
        );
        Mockito
                .when(executorService.schedule(Mockito.any(Runnable.class), Mockito.anyLong(), Mockito.any()))
                .thenReturn(null);
    }

    @Test
    public void shouldScheduleCardUpdateForTheClosestAvailableTimeWithEqualTimeUnit() throws IncorrectCardPriorityScheduleException {
        // arrange
        final String cardToSchedule = "card1";
        Mockito.when(cardRegistry.isCardExist(cardToSchedule)).thenReturn(true);
        // act
        final LocalDateTime testBeginning = LocalDateTime.of(2007, Month.AUGUST, 21, 12, 40, 10);
        try {
            cardsUpdateScheduler.scheduleCardPriorityUpdateFrom(
                    testBeginning,
                    cardToSchedule,
                    new CardPriorityUpdateSchedule(testTimeUnit, 1L)
            );
        } catch (NotExistingCardException e) {
            Assert.fail(e.getMessage());
        }
        // assert
        final LocalDateTime expectedScheduledTime = LocalDateTime.of(2007, Month.AUGUST, 21, 13, 0);
        final Set<String> actualScheduledCards = cardsUpdateScheduler.getActiveScheduleFor(expectedScheduledTime);

        Assert.assertTrue(actualScheduledCards.stream().anyMatch(cardName -> cardName.equals(cardToSchedule)));
    }

    @Test
    public void shouldScheduleCardUpdateForTheClosestAvailableTimeWithSmallerTimeUnit() throws IncorrectCardPriorityScheduleException {
        // arrange
        final String cardToSchedule = "card1";
        Mockito.when(cardRegistry.isCardExist(cardToSchedule)).thenReturn(true);
        // act
        final LocalDateTime testBeginning = LocalDateTime.of(2007, Month.AUGUST, 21, 12, 30, 10);
        try {
            cardsUpdateScheduler.scheduleCardPriorityUpdateFrom(
                    testBeginning,
                    cardToSchedule,
                    new CardPriorityUpdateSchedule(TimeUnit.MINUTES, 1L)
            );
        } catch (NotExistingCardException e) {
            Assert.fail(e.getMessage());
        }
        // assert
        final LocalDateTime expectedScheduledTime = LocalDateTime.of(2007, Month.AUGUST, 21, 12, 31);
        final Set<String> actualScheduledCards = cardsUpdateScheduler.getActiveScheduleFor(expectedScheduledTime);

        Assert.assertTrue(actualScheduledCards.stream().anyMatch(cardName -> cardName.equals(cardToSchedule)));
    }

    @Test
    public void shouldScheduleCardUpdateForTheClosestAvailableTimeWithLargerTimeUnit() throws IncorrectCardPriorityScheduleException {
        // arrange
        final String cardToSchedule = "card1";
        Mockito.when(cardRegistry.isCardExist(cardToSchedule)).thenReturn(true);
        // act
        final LocalDateTime testBeginning = LocalDateTime.of(2007, Month.AUGUST, 21, 12, 30, 10);
        try {
            cardsUpdateScheduler.scheduleCardPriorityUpdateFrom(
                    testBeginning,
                    cardToSchedule,
                    new CardPriorityUpdateSchedule(TimeUnit.DAYS, 1L)
            );
        } catch (NotExistingCardException e) {
            Assert.fail(e.getMessage());
        }
        // assert
        final LocalDateTime expectedScheduledTime = LocalDateTime.of(2007, Month.AUGUST, 22, 0, 0);
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
    public void shouldScheduleTasksWithSameScheduleForSameTime() throws Exception {
        // arrange
        Mockito.when(cardRegistry.isCardExist(Mockito.anyString())).thenReturn(true);
        final String firstCard = "card1";
        final LocalDateTime firstCardCreationTime = LocalDateTime.of(2007, Month.AUGUST, 21, 12, 30);
        final String secondCard = "card2";
        final LocalDateTime secondCardCreationTime = LocalDateTime.of(2007, Month.AUGUST, 21, 12, 40);
        // act
        cardsUpdateScheduler.scheduleCardPriorityUpdateFrom(
                firstCardCreationTime,
                firstCard,
                new CardPriorityUpdateSchedule(testTimeUnit, 1L)
        );
        cardsUpdateScheduler.scheduleCardPriorityUpdateFrom(
                secondCardCreationTime,
                secondCard,
                new CardPriorityUpdateSchedule(testTimeUnit, 1L)
        );
        // assert
        final LocalDateTime expectedScheduledTime = LocalDateTime.of(2007, Month.AUGUST, 21, 13, 0);
        final Set<String> actualScheduledCards = cardsUpdateScheduler.getActiveScheduleFor(expectedScheduledTime);
        Assert.assertEquals(2, actualScheduledCards.size());
    }

    @Test
    public void shouldScheduleTasksWithDifferentScheduleForSameTime() throws Exception {
        // arrange
        Mockito.when(cardRegistry.isCardExist(Mockito.anyString())).thenReturn(true);
        final String longRunningCard = "card1";
        final LocalDateTime longCardCreationTime = LocalDateTime.of(2007, Month.AUGUST, 21, 12, 30);
        cardsUpdateScheduler.scheduleCardPriorityUpdateFrom(
                longCardCreationTime,
                longRunningCard,
                new CardPriorityUpdateSchedule(testTimeUnit, 3L)
        );
        final String shortRunningCard = "card2";
        final LocalDateTime shortCardCreationTime = LocalDateTime.of(2007, Month.AUGUST, 21, 12, 40);
        // act
        cardsUpdateScheduler.scheduleCardPriorityUpdateFrom(
                shortCardCreationTime,
                shortRunningCard,
                new CardPriorityUpdateSchedule(testTimeUnit, 1L)
        );
        final LocalDateTime firstIteration = LocalDateTime.of(2007, Month.AUGUST, 21, 13, 0);
        cardsUpdateScheduler.updateCardsNewPriorityLevel(firstIteration);
        final LocalDateTime secondIteration = LocalDateTime.of(2007, Month.AUGUST, 21, 14, 0);
        cardsUpdateScheduler.updateCardsNewPriorityLevel(secondIteration);
        // assert
        final LocalDateTime expectedScheduledTime = LocalDateTime.of(2007, Month.AUGUST, 21, 15, 0);
        final Set<String> actualScheduledCards = cardsUpdateScheduler.getActiveScheduleFor(expectedScheduledTime);
        Assert.assertEquals(2, actualScheduledCards.size());
    }

    @Test
    public void shouldScheduleTasksWithMatchingScheduleTimeUnitForSameTime() throws Exception {
        // arrange
        Mockito.when(cardRegistry.isCardExist(Mockito.anyString())).thenReturn(true);
        final String longRunningCard = "card1";
        final LocalDateTime longCardCreationTime = LocalDateTime.of(2007, Month.AUGUST, 21, 12, 30);
        final String shortRunningCard = "card2";
        final LocalDateTime shortCardCreationTime = LocalDateTime.of(2007, Month.AUGUST, 21, 12, 40);
        // act
        cardsUpdateScheduler.scheduleCardPriorityUpdateFrom(
                longCardCreationTime,
                longRunningCard,
                new CardPriorityUpdateSchedule(TimeUnit.DAYS, 1L)
        );
        cardsUpdateScheduler.scheduleCardPriorityUpdateFrom(
                shortCardCreationTime,
                shortRunningCard,
                new CardPriorityUpdateSchedule(testTimeUnit, 12L)
        );
        // assert
        final LocalDateTime expectedScheduledTime = LocalDateTime.of(2007, Month.AUGUST, 22, 0, 0);
        final Set<String> actualScheduledCards = cardsUpdateScheduler.getActiveScheduleFor(expectedScheduledTime);
        Assert.assertEquals(2, actualScheduledCards.size());
    }

    @Test
    public void shouldScheduleTasksWithDifferentScheduleTimeUnitForDifferentTime() throws Exception {
        // arrange
        Mockito.when(cardRegistry.isCardExist(Mockito.anyString())).thenReturn(true);
        final String longRunningCard = "card1";
        final LocalDateTime longCardCreationTime = LocalDateTime.of(2007, Month.AUGUST, 21, 12, 30);
        cardsUpdateScheduler.scheduleCardPriorityUpdateFrom(
                longCardCreationTime,
                longRunningCard,
                new CardPriorityUpdateSchedule(TimeUnit.DAYS, 1L)
        );
        final String shortRunningCard = "card2";
        final LocalDateTime shortCardCreationTime = LocalDateTime.of(2007, Month.AUGUST, 21, 10, 40);
        // act
        cardsUpdateScheduler.scheduleCardPriorityUpdateFrom(
                shortCardCreationTime,
                shortRunningCard,
                new CardPriorityUpdateSchedule(testTimeUnit, 8L)
        );
        final LocalDateTime firstIteration = LocalDateTime.of(2007, Month.AUGUST, 21, 18, 0);
        cardsUpdateScheduler.updateCardsNewPriorityLevel(firstIteration);
        // assert
        final LocalDateTime longRunningScheduledTime = LocalDateTime.of(2007, Month.AUGUST, 22, 0, 0);
        final Set<String> longRunningScheduledCards = cardsUpdateScheduler.getActiveScheduleFor(longRunningScheduledTime);
        Assert.assertEquals(1, longRunningScheduledCards.size());

        final LocalDateTime expectedScheduledTime = LocalDateTime.of(2007, Month.AUGUST, 22, 2, 0);
        final Set<String> actualScheduledCards = cardsUpdateScheduler.getActiveScheduleFor(expectedScheduledTime);
        Assert.assertEquals(1, actualScheduledCards.size());
    }
}
