package org.cards_tracker.service;

import org.cards_tracker.domain.CardPriority;
import org.cards_tracker.error.CardAlreadyExistsException;
import org.cards_tracker.error.IncorrectCardTitleException;
import org.cards_tracker.error.NotExistingCardException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ScheduledInMemoryCardRegistryTest {

    private ScheduledInMemoryCardRegistry cardRegistry;

    @Before
    public void setUp() {
        cardRegistry = new ScheduledInMemoryCardRegistry(TimeUnit.MINUTES, 10L);
        cardRegistry.terminate();
    }

    @Test
    public void shouldReturnPrioritizedCardsList() throws Exception {
        // arrange
        cardRegistry.createCard("card1", CardPriority.I_NEED_TO_DO_IT_TODAY_OR_TOMORROW);
        cardRegistry.createCard("card2", CardPriority.LEAVE_EVERYTHING_AND_START_WORKING_ON_IT);
        cardRegistry.createCard("card3", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY);
        cardRegistry.createCard("card4", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY);
        // act
        final List<String> actualCardsList = cardRegistry.getPrioritizedCards();
        // assert
        Assert.assertEquals(4, actualCardsList.size());
        Assert.assertEquals("card2", actualCardsList.get(0));
        Assert.assertEquals("card3", actualCardsList.get(1));
        Assert.assertEquals("card4", actualCardsList.get(2));
        Assert.assertEquals("card1", actualCardsList.get(3));
    }

    @Test
    public void shouldUpdateCardsPriority() throws Exception {
        // arrange
        cardRegistry.createCard("card1", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY);
        cardRegistry.createCard("card2", CardPriority.LEAVE_EVERYTHING_AND_START_WORKING_ON_IT);
        cardRegistry.createCard("card3", CardPriority.I_NEED_TO_DO_IT_TODAY_OR_TOMORROW);
        cardRegistry.createCard("card4", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY);
        // act
        cardRegistry.updateCardsPriority();
        // assert
        final List<String> actualCardsList = cardRegistry.getPrioritizedCards();
        Assert.assertEquals(4, actualCardsList.size());
        Assert.assertEquals("card1", actualCardsList.get(0));
        Assert.assertEquals("card2", actualCardsList.get(1));
        Assert.assertEquals("card4", actualCardsList.get(2));
        Assert.assertEquals("card3", actualCardsList.get(3));
    }

    @Test
    public void shouldBottomCardPriority() throws Exception {
        // arrange
        cardRegistry.createCard("card1", CardPriority.I_NEED_TO_DO_IT_TODAY_OR_TOMORROW);
        cardRegistry.createCard("card2", CardPriority.LEAVE_EVERYTHING_AND_START_WORKING_ON_IT);
        cardRegistry.createCard("card3", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY);
        cardRegistry.createCard("card4", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY);
        // act
        cardRegistry.bottomCardPriority("card2");
        // assert
        final List<String> actualCardsList = cardRegistry.getPrioritizedCards();
        Assert.assertEquals(4, actualCardsList.size());
        Assert.assertEquals("card3", actualCardsList.get(0));
        Assert.assertEquals("card4", actualCardsList.get(1));
        Assert.assertEquals("card1", actualCardsList.get(2));
        Assert.assertEquals("card2", actualCardsList.get(3));
    }

    @Test
    public void shouldThrowAnExceptionForNonExistingCardName() {
        // arrange
        final String cardName = "card1";
        // act
        try {
            cardRegistry.bottomCardPriority(cardName);
            // assert
            Assert.fail("An exception should be thrown.");
        } catch (NotExistingCardException e) {
            // do nothing
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void shouldThrowAnExceptionForDuplicatedCardName() throws Exception {
        // arrange
        final String cardName = "card1";
        final CardPriority anyPriority = CardPriority.I_NEED_TO_DO_IT_TODAY_OR_TOMORROW;
        cardRegistry.createCard(cardName, anyPriority);
        // act
        try {
            cardRegistry.createCard(cardName, anyPriority);
            // assert
            Assert.fail("An exception should be thrown.");
        } catch (CardAlreadyExistsException e) {
            // do nothing
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void shouldThrowAnExceptionForIncorrectCardName() {
        // arrange
        final String cardName = "";
        final CardPriority anyPriority = CardPriority.I_NEED_TO_DO_IT_TODAY_OR_TOMORROW;
        // act
        try {
            cardRegistry.createCard(cardName, anyPriority);
            // assert
            Assert.fail("An exception should be thrown.");
        } catch (IncorrectCardTitleException e) {
            // do nothing
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertFalse(cardRegistry.isCardExist(cardName));
    }

    @Test
    public void shouldRemoveCard() throws Exception {
        // arrange
        final String cardName = "card1";
        final CardPriority anyPriority = CardPriority.I_NEED_TO_DO_IT_TODAY_OR_TOMORROW;
        cardRegistry.createCard(cardName, anyPriority);
        // act
        cardRegistry.removeCard(cardName);
        // assert
        Assert.assertFalse(cardRegistry.isCardExist(cardName));
    }

    @Test
    public void shouldRemoveNonExistingCard() {
        // arrange
        final String cardName = "card1";
        // act
        cardRegistry.removeCard(cardName);
        // assert
        Assert.assertFalse(cardRegistry.isCardExist(cardName));
    }
}