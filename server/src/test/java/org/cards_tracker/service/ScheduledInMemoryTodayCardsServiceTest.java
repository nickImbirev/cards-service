package org.cards_tracker.service;

import org.cards_tracker.error.NotExistingCardException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RunWith(MockitoJUnitRunner.class)
public class ScheduledInMemoryTodayCardsServiceTest {
    private ScheduledInMemoryTodayCardsService todayCardsService;
    @Mock
    private CardRegistry cardRegistry;

    @Test
    public void shouldReturnLimitedCardsForToday() {
        // arrange
        final int maxCardsForToday = 2;
        todayCardsService =
                new ScheduledInMemoryTodayCardsService(cardRegistry, TimeUnit.MINUTES, 10L, maxCardsForToday);
        todayCardsService.terminate();

        final List<String> prioritizedCards = List.of(
                "card1",
                "card2",
                "card3"
        );
        Mockito.when(cardRegistry.getPrioritizedCards()).thenReturn(prioritizedCards);
        todayCardsService.fillTheCardsForToday();
        // act
        final List<String> actualTodayCards = todayCardsService.getCardsForToday();
        // assert
        Assert.assertEquals(maxCardsForToday, actualTodayCards.size());
        prioritizedCards
                .subList(0, maxCardsForToday)
                .forEach(prioritizedCard -> Assert.assertTrue(actualTodayCards
                        .stream()
                        .anyMatch(name -> Objects.equals(name, prioritizedCard))
                ));
    }

    @Test
    public void shouldReturnEmptyList() {
        // arrange
        todayCardsService =
                new ScheduledInMemoryTodayCardsService(cardRegistry, TimeUnit.MINUTES, 10L, 2);
        todayCardsService.terminate();

        Mockito.when(cardRegistry.getPrioritizedCards()).thenReturn(List.of());
        todayCardsService.fillTheCardsForToday();
        // act
        final List<String> actualTodayCards = todayCardsService.getCardsForToday();
        // assert
        Assert.assertTrue(actualTodayCards.isEmpty());
    }

    @Test
    public void shouldAddAdditionalCardForToday() throws Exception {
        // arrange
        todayCardsService =
                new ScheduledInMemoryTodayCardsService(cardRegistry, TimeUnit.MINUTES, 10L, 2);
        todayCardsService.terminate();

        Mockito.when(cardRegistry.getPrioritizedCards()).thenReturn(List.of());
        todayCardsService.fillTheCardsForToday();

        final String additionalCardTitle = "card1";
        Mockito.when(cardRegistry.isCardExist(additionalCardTitle)).thenReturn(true);
        // act
        todayCardsService.addAdditionalCardForToday(additionalCardTitle);
        // assert
        final List<String> actualTodayCards = todayCardsService.getCardsForToday();
        Assert.assertEquals(1, actualTodayCards.size());
        Assert.assertEquals(additionalCardTitle, actualTodayCards.get(0));
    }

    @Test
    public void shouldThrowExceptionForNonExistingCardTitle() {
        // arrange
        todayCardsService =
                new ScheduledInMemoryTodayCardsService(cardRegistry, TimeUnit.MINUTES, 10L, 2);
        todayCardsService.terminate();

        Mockito.when(cardRegistry.getPrioritizedCards()).thenReturn(List.of());
        todayCardsService.fillTheCardsForToday();

        final String additionalCardTitle = "card1";
        Mockito.when(cardRegistry.isCardExist(additionalCardTitle)).thenReturn(false);
        // act
        try {
            todayCardsService.addAdditionalCardForToday(additionalCardTitle);
            Assert.fail("An exception should be thrown.");
        } catch (NotExistingCardException e) {
            // do nothing
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        // assert
        final List<String> actualTodayCards = todayCardsService.getCardsForToday();
        Assert.assertTrue(actualTodayCards.isEmpty());
    }

    @Test
    public void shouldCompleteTodayCard() throws Exception {
        // arrange
        todayCardsService =
                new ScheduledInMemoryTodayCardsService(cardRegistry, TimeUnit.MINUTES, 10L, 2);
        todayCardsService.terminate();

        final String todayCardName = "card1";
        Mockito.when(cardRegistry.getPrioritizedCards()).thenReturn(List.of(
                todayCardName
        ));
        todayCardsService.fillTheCardsForToday();

        Mockito.doNothing().when(cardRegistry).bottomCardPriority(todayCardName);
        // act
        todayCardsService.completeCardForToday(todayCardName);
        // assert
        final List<String> actualTodayCards = todayCardsService.getCardsForToday();
        Assert.assertTrue(actualTodayCards.isEmpty());
    }

    @Test
    public void shouldThrowExceptionForNonExistingCompletedCardName() {
        // arrange
        todayCardsService =
                new ScheduledInMemoryTodayCardsService(cardRegistry, TimeUnit.MINUTES, 10L, 2);
        todayCardsService.terminate();

        Mockito.when(cardRegistry.getPrioritizedCards()).thenReturn(List.of());
        todayCardsService.fillTheCardsForToday();

        final String nonExistingCardName = "card1";
        // act
        try {
            todayCardsService.completeCardForToday(nonExistingCardName);
            Assert.fail("An exception should be thrown.");
        } catch (NotExistingCardException e) {
            // do nothing
        }  catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        // assert
        final List<String> actualTodayCards = todayCardsService.getCardsForToday();
        Assert.assertTrue(actualTodayCards.isEmpty());
    }

    @Test
    public void shouldThrowExceptionForNonRegisteredCardName() throws Exception {
        // arrange
        todayCardsService =
                new ScheduledInMemoryTodayCardsService(cardRegistry, TimeUnit.MINUTES, 10L, 2);
        todayCardsService.terminate();

        String todayCardName = "card1";
        Mockito.when(cardRegistry.getPrioritizedCards()).thenReturn(List.of(
                todayCardName
        ));
        todayCardsService.fillTheCardsForToday();
        Mockito
                .doThrow(new NotExistingCardException(todayCardName))
                .when(cardRegistry).bottomCardPriority(todayCardName);
        // act
        try {
            todayCardsService.completeCardForToday(todayCardName);
            Assert.fail("An exception should be thrown.");
        } catch (NotExistingCardException e) {
            // do nothing
        }  catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        // assert
        final List<String> actualTodayCards = todayCardsService.getCardsForToday();
        Assert.assertTrue(actualTodayCards.isEmpty());
    }
}