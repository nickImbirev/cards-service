package org.cards_tracker.service;

import org.cards_tracker.domain.Card;
import org.cards_tracker.domain.CardPriority;
import org.cards_tracker.error.CardAlreadyExistsException;
import org.cards_tracker.error.IncorrectCardTitleException;
import org.cards_tracker.error.NotExistingCardException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RunWith(MockitoJUnitRunner.class)
public class ScheduledInMemoryTodayCardsServiceTest {

    private ScheduledInMemoryTodayCardsService todayCardsService;

    @Mock
    private CardRegistry cardRegistry;
    @Mock
    private ScheduledExecutorService delayedTasksExecutor;

    @Before
    public void setUp() {
        todayCardsService = new ScheduledInMemoryTodayCardsService(
                delayedTasksExecutor,
                cardRegistry,
                TimeUnit.MINUTES,
                Long.MAX_VALUE,
                Integer.MAX_VALUE);
        // disable scheduling
        Mockito
                .when(delayedTasksExecutor.scheduleWithFixedDelay(Mockito.any(), Mockito.anyLong(), Mockito.anyInt(), Mockito.any()))
                .thenReturn(null);
    }

    @Test
    public void shouldReturnPrioritizedMaxTodayCards() throws IncorrectCardTitleException {
        // arrange
        int maxTodayCards = 3;
        todayCardsService = new ScheduledInMemoryTodayCardsService(
                delayedTasksExecutor,
                cardRegistry,
                TimeUnit.MINUTES,
                Long.MAX_VALUE,
                maxTodayCards);
        final List<Card> prioritizedCards = List.of(
                new Card("card1", CardPriority.LEAVE_EVERYTHING_AND_START_WORKING_ON_IT),
                new Card("card2", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY),
                new Card("card3", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY),
                new Card("card4", CardPriority.I_NEED_TO_DO_IT_TODAY_OR_TOMORROW),
                new Card("card5", CardPriority.I_NEED_TO_DO_IT_TODAY_OR_TOMORROW)
        );
        Mockito.when(cardRegistry.getPrioritizedCards()).thenReturn(prioritizedCards);
        todayCardsService.fillTheCardsForToday();
        // act
        final List<String> actualCards = todayCardsService.getCardsForToday();
        // assert
        Assert.assertEquals(maxTodayCards, actualCards.size());
        Assert.assertEquals(prioritizedCards.get(0).getTitle(), actualCards.get(0));
        Assert.assertEquals(prioritizedCards.get(1).getTitle(), actualCards.get(1));
        Assert.assertEquals(prioritizedCards.get(2).getTitle(), actualCards.get(2));
    }

    @Test
    public void shouldReturnEmptyCardsList() {
        // arrange
        Mockito.when(cardRegistry.getPrioritizedCards()).thenReturn(List.of());
        todayCardsService.fillTheCardsForToday();
        // act
        final List<String> actualCards = todayCardsService.getCardsForToday();
        // assert
        Assert.assertTrue(actualCards.isEmpty());
    }

    @Test
    public void shouldAddCardForTodayEvenIfTheMaxReached() throws IncorrectCardTitleException, NotExistingCardException {
        // arrange
        int maxTodayCards = 3;
        todayCardsService = new ScheduledInMemoryTodayCardsService(
                delayedTasksExecutor,
                cardRegistry,
                TimeUnit.MINUTES,
                Long.MAX_VALUE,
                maxTodayCards);
        final List<Card> prioritizedCards = List.of(
                new Card("card1", CardPriority.I_NEED_TO_DO_IT_TODAY_OR_TOMORROW),
                new Card("card2", CardPriority.I_NEED_TO_DO_IT_TODAY_OR_TOMORROW),
                new Card("card3", CardPriority.I_NEED_TO_DO_IT_TODAY_OR_TOMORROW),
                new Card("card4", CardPriority.I_NEED_TO_DO_IT_TODAY_OR_TOMORROW),
                new Card("card5", CardPriority.I_NEED_TO_DO_IT_TODAY_OR_TOMORROW)
        );
        Mockito.when(cardRegistry.getPrioritizedCards()).thenReturn(prioritizedCards);
        final String addedCardTitle = prioritizedCards.get(3).getTitle();
        Mockito.when(cardRegistry.isCardExist(addedCardTitle)).thenReturn(true);
        todayCardsService.fillTheCardsForToday();
        // act
        todayCardsService.addAdditionalCardForToday(addedCardTitle);
        // assert
        final List<String> actualCards = todayCardsService.getCardsForToday();

        Assert.assertEquals(maxTodayCards + 1, actualCards.size());
        Assert.assertTrue(actualCards.stream().anyMatch(title -> title.equals(addedCardTitle)));
    }

    @Test
    public void shouldNotAddNonExistingCardForToday() {
        // arrange
        Mockito.when(cardRegistry.getPrioritizedCards()).thenReturn(List.of());
        final String nonExistingCard = "test";
        Mockito.when(cardRegistry.isCardExist(nonExistingCard)).thenReturn(false);
        todayCardsService.fillTheCardsForToday();
        // act
        try {
            todayCardsService.addAdditionalCardForToday(nonExistingCard);
            Assert.fail("An error should be thrown.");
        } catch (NotExistingCardException e) {
            // do nothing
        }
        // assert
        final List<String> actualCards = todayCardsService.getCardsForToday();
        Assert.assertTrue(actualCards.stream().noneMatch(title -> title.equals(nonExistingCard)));
    }

    @Test
    public void shouldCompleteTodayCard() throws IncorrectCardTitleException, NotExistingCardException {
        // arrange
        final List<Card> prioritizedCards = List.of(
                new Card("card1", CardPriority.LEAVE_EVERYTHING_AND_START_WORKING_ON_IT),
                new Card("card2", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY),
                new Card("card3", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY)
        );
        Mockito.when(cardRegistry.getPrioritizedCards()).thenReturn(prioritizedCards);
        final String completedCard = prioritizedCards.get(1).getTitle();
        Mockito.doNothing().when(cardRegistry).bottomCardPriority(completedCard);
        todayCardsService.fillTheCardsForToday();
        // act
        try {
            todayCardsService.completeCardForToday(completedCard);
        } catch (NotExistingCardException e) {
            Assert.fail(e.getMessage());
        }
        // assert
        final List<String> actualCards = todayCardsService.getCardsForToday();
        Assert.assertTrue(actualCards.stream().noneMatch(title -> title.equals(completedCard)));
    }

    @Test
    public void shouldCompleteEvenNotExistingCard() throws IncorrectCardTitleException, NotExistingCardException {
        // arrange
        final List<Card> prioritizedCards = List.of(
                new Card("card1", CardPriority.LEAVE_EVERYTHING_AND_START_WORKING_ON_IT),
                new Card("card2", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY),
                new Card("card3", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY)
        );
        Mockito.when(cardRegistry.getPrioritizedCards()).thenReturn(prioritizedCards);
        final String completedCard = prioritizedCards.get(1).getTitle();
        Mockito.doThrow(NotExistingCardException.class).when(cardRegistry).bottomCardPriority(completedCard);
        todayCardsService.fillTheCardsForToday();
        // act
        try {
            todayCardsService.completeCardForToday(completedCard);
        } catch (NotExistingCardException e) {
            Assert.fail(e.getMessage());
        }
        // assert
        final List<String> actualCards = todayCardsService.getCardsForToday();
        Assert.assertTrue(actualCards.stream().noneMatch(title -> title.equals(completedCard)));
    }

    @Test
    public void shouldNotCompleteNotExistingCard() {
        // arrange
        Mockito.when(cardRegistry.getPrioritizedCards()).thenReturn(List.of());
        todayCardsService.fillTheCardsForToday();
        // act
        try {
            final String notExistingCard = "test1";
            todayCardsService.completeCardForToday(notExistingCard);
            // assert
            Assert.fail("An error should be thrown.");
        } catch (NotExistingCardException e) {
            // do nothing
        }
    }

    @Test
    public void shouldReshuffleTodayCards() throws IncorrectCardTitleException {
        // arrange
        final List<Card> prioritizedCards = List.of(
                new Card("card1", CardPriority.LEAVE_EVERYTHING_AND_START_WORKING_ON_IT),
                new Card("card2", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY),
                new Card("card3", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY),
                new Card("card4", CardPriority.I_NEED_TO_DO_IT_TODAY_OR_TOMORROW),
                new Card("card5", CardPriority.I_NEED_TO_DO_IT_TODAY_OR_TOMORROW)
        );
        Mockito.when(cardRegistry.getPrioritizedCards()).thenReturn(prioritizedCards);
        todayCardsService.fillTheCardsForToday();
        // act
        final List<String> updatedCards = List.of(
                prioritizedCards.get(4).getTitle(),
                prioritizedCards.get(2).getTitle(),
                prioritizedCards.get(0).getTitle(),
                prioritizedCards.get(1).getTitle(),
                prioritizedCards.get(3).getTitle()
        );
        try {
            todayCardsService.reshuffleTodayCards(updatedCards);
        } catch (NotExistingCardException | CardAlreadyExistsException e) {
            Assert.fail(e.getMessage());
        }
        // assert
        final List<String> actualCards = todayCardsService.getCardsForToday();

        Assert.assertEquals(prioritizedCards.size(), actualCards.size());
        Assert.assertEquals(updatedCards.get(0), actualCards.get(0));
        Assert.assertEquals(updatedCards.get(1), actualCards.get(1));
        Assert.assertEquals(updatedCards.get(2), actualCards.get(2));
        Assert.assertEquals(updatedCards.get(3), actualCards.get(3));
        Assert.assertEquals(updatedCards.get(4), actualCards.get(4));
    }

    @Test
    public void shouldNotReshuffleDuplicatedCards() throws IncorrectCardTitleException {
        // arrange
        final List<Card> prioritizedCards = List.of(
                new Card("card1", CardPriority.LEAVE_EVERYTHING_AND_START_WORKING_ON_IT),
                new Card("card2", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY),
                new Card("card3", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY),
                new Card("card4", CardPriority.I_NEED_TO_DO_IT_TODAY_OR_TOMORROW),
                new Card("card5", CardPriority.I_NEED_TO_DO_IT_TODAY_OR_TOMORROW)
        );
        Mockito.when(cardRegistry.getPrioritizedCards()).thenReturn(prioritizedCards);
        todayCardsService.fillTheCardsForToday();
        // act
        final List<String> updatedCards = List.of(
                prioritizedCards.get(4).getTitle(),
                prioritizedCards.get(4).getTitle(),
                prioritizedCards.get(4).getTitle(),
                prioritizedCards.get(1).getTitle(),
                prioritizedCards.get(3).getTitle()
        );
        try {
            todayCardsService.reshuffleTodayCards(updatedCards);
            Assert.fail("An error should be thrown.");
        } catch (NotExistingCardException e) {
            Assert.fail(e.getMessage());
        } catch (CardAlreadyExistsException e) {
            // do nothing
        }
        // assert
        final List<String> actualCards = todayCardsService.getCardsForToday();

        Assert.assertEquals(prioritizedCards.size(), actualCards.size());
        Assert.assertEquals(prioritizedCards.get(0).getTitle(), actualCards.get(0));
        Assert.assertEquals(prioritizedCards.get(1).getTitle(), actualCards.get(1));
        Assert.assertEquals(prioritizedCards.get(2).getTitle(), actualCards.get(2));
        Assert.assertEquals(prioritizedCards.get(3).getTitle(), actualCards.get(3));
        Assert.assertEquals(prioritizedCards.get(4).getTitle(), actualCards.get(4));
    }

    @Test
    public void shouldNotReshuffleNotExistingCards() throws IncorrectCardTitleException {
        // arrange
        final List<Card> prioritizedCards = List.of(
                new Card("card1", CardPriority.LEAVE_EVERYTHING_AND_START_WORKING_ON_IT),
                new Card("card2", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY),
                new Card("card3", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY),
                new Card("card4", CardPriority.I_NEED_TO_DO_IT_TODAY_OR_TOMORROW),
                new Card("card5", CardPriority.I_NEED_TO_DO_IT_TODAY_OR_TOMORROW)
        );
        Mockito.when(cardRegistry.getPrioritizedCards()).thenReturn(prioritizedCards);
        todayCardsService.fillTheCardsForToday();
        // act
        final List<String> updatedCards = List.of(
                prioritizedCards.get(0).getTitle(),
                prioritizedCards.get(2).getTitle(),
                prioritizedCards.get(3).getTitle(),
                prioritizedCards.get(1).getTitle(),
                "weird card"
        );
        try {
            todayCardsService.reshuffleTodayCards(updatedCards);
            Assert.fail("An error should be thrown.");
        } catch (NotExistingCardException e) {
            // do nothing
        } catch (CardAlreadyExistsException e) {
            Assert.fail(e.getMessage());
        }
        // assert
        final List<String> actualCards = todayCardsService.getCardsForToday();

        Assert.assertEquals(prioritizedCards.size(), actualCards.size());
        Assert.assertEquals(prioritizedCards.get(0).getTitle(), actualCards.get(0));
        Assert.assertEquals(prioritizedCards.get(1).getTitle(), actualCards.get(1));
        Assert.assertEquals(prioritizedCards.get(2).getTitle(), actualCards.get(2));
        Assert.assertEquals(prioritizedCards.get(3).getTitle(), actualCards.get(3));
        Assert.assertEquals(prioritizedCards.get(4).getTitle(), actualCards.get(4));
    }

    @Test
    public void shouldNotReshuffleNotAllCards() throws IncorrectCardTitleException {
        // arrange
        final List<Card> prioritizedCards = List.of(
                new Card("card1", CardPriority.LEAVE_EVERYTHING_AND_START_WORKING_ON_IT),
                new Card("card2", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY),
                new Card("card3", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY),
                new Card("card4", CardPriority.I_NEED_TO_DO_IT_TODAY_OR_TOMORROW),
                new Card("card5", CardPriority.I_NEED_TO_DO_IT_TODAY_OR_TOMORROW)
        );
        Mockito.when(cardRegistry.getPrioritizedCards()).thenReturn(prioritizedCards);
        todayCardsService.fillTheCardsForToday();
        // act
        final List<String> updatedCards = List.of(
                prioritizedCards.get(4).getTitle(),
                prioritizedCards.get(1).getTitle()
        );
        try {
            todayCardsService.reshuffleTodayCards(updatedCards);
            Assert.fail("An error should be thrown.");
        } catch (NotExistingCardException e) {
            // do nothing
        } catch (CardAlreadyExistsException e) {
            Assert.fail(e.getMessage());
        }
        // assert
        final List<String> actualCards = todayCardsService.getCardsForToday();

        Assert.assertEquals(prioritizedCards.size(), actualCards.size());
        Assert.assertEquals(prioritizedCards.get(0).getTitle(), actualCards.get(0));
        Assert.assertEquals(prioritizedCards.get(1).getTitle(), actualCards.get(1));
        Assert.assertEquals(prioritizedCards.get(2).getTitle(), actualCards.get(2));
        Assert.assertEquals(prioritizedCards.get(3).getTitle(), actualCards.get(3));
        Assert.assertEquals(prioritizedCards.get(4).getTitle(), actualCards.get(4));
    }
}