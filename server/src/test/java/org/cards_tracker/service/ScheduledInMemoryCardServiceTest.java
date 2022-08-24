package org.cards_tracker.service;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class ScheduledInMemoryCardServiceTest {

    private ScheduledInMemoryCardService cardService;

    @Test
    public void shouldReturnMorePrioritizedCardsForSecondIteration() {
        // arrange
        final List<String> createdCards = List.of("test", "test2", "test3", "test4");
        int dayLimit = createdCards.size() - 2;
        cardService = new ScheduledInMemoryCardService(
                TimeUnit.SECONDS, 10L,
                dayLimit
        );
        createdCards.forEach(cardTitle -> {
            try {
                cardService.createCard(cardTitle);
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
        });
        cardService.updateTodayCards();
        final List<String> firstIterationCards = cardService.getCardsForToday();
        firstIterationCards.forEach(card -> {
            try {
                cardService.completeCardForToday(card);
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
        });
        // act
        cardService.updateTodayCards();
        List<String> expectedCards = createdCards
                .stream()
                .filter(card -> !firstIterationCards.contains(card))
                .collect(Collectors.toList());
        final List<String> secondIterationCards = cardService.getCardsForToday();
        // assert
        Assert.assertEquals(expectedCards.size(), secondIterationCards.size());
        expectedCards.forEach(expectedCard -> Assert.assertEquals(
                expectedCard,
                secondIterationCards.stream()
                        .filter(expectedCard::equals)
                        .findFirst()
                        .get()
        ));
    }

    @Test
    public void shouldAddAdditionalCardForTheIteration() {
        // arrange
        final List<String> createdCards = List.of("test", "test2", "test3", "test4");
        int dayLimit = createdCards.size() - 2;
        cardService = new ScheduledInMemoryCardService(
                TimeUnit.SECONDS, 10L,
                dayLimit
        );
        createdCards.forEach(cardTitle -> {
            try {
                cardService.createCard(cardTitle);
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
        });
        cardService.updateTodayCards();
        final List<String> todayCardsBefore = cardService.getCardsForToday();
        // act
        final String additionalCard = createdCards.stream()
                .filter(card -> !todayCardsBefore.contains(card))
                .findAny()
                .get();
        try {
            cardService.addAdditionalCardForToday(additionalCard);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        // assert
        final List<String> actualCardsForToday = cardService.getCardsForToday();
        final List<String> expectedCardsForToday = new ArrayList<>(todayCardsBefore);
        expectedCardsForToday.add(additionalCard);
        Assert.assertEquals(expectedCardsForToday.size(), actualCardsForToday.size());
        expectedCardsForToday.forEach(card ->
                Assert.assertTrue(actualCardsForToday.stream().anyMatch(actualCard -> actualCard.equals(card)))
        );
    }

    @Test
    public void shouldRemoveCardCompletely() {
        // arrange
        final String cardToRemove = "test";
        final List<String> createdCards = List.of(cardToRemove, "test2", "test3", "test4");
        cardService = new ScheduledInMemoryCardService(
                TimeUnit.SECONDS, 10L,
                createdCards.size()
        );
        createdCards.forEach(cardTitle -> {
            try {
                cardService.createCard(cardTitle);
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
        });
        cardService.updateTodayCards();
        final List<String> cardsBeforeDelete = cardService.getCardsForToday();
        // act
        cardService.removeCard(cardToRemove);
        // assert
        final List<String> cardsAfterDelete = cardService.getCardsForToday();
        Assert.assertEquals(cardsBeforeDelete.size() - 1 , cardsAfterDelete.size());
        Assert.assertFalse(cardsAfterDelete.contains(cardToRemove));
    }
}