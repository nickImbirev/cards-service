package org.cards_tracker.service;

import org.cards_tracker.domain.Card;
import org.cards_tracker.domain.CardPriority;
import org.cards_tracker.error.CardAlreadyExistsException;
import org.cards_tracker.error.IncorrectCardTitleException;
import org.cards_tracker.error.NotExistingCardException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class InMemoryCardRegistryTest {

    private InMemoryCardRegistry cardRegistry;

    @Before
    public void setUp() {
        cardRegistry = new InMemoryCardRegistry();
    }

    @Test
    public void shouldReturnPrioritizedCardsList() throws Exception {
        // arrange
        cardRegistry.createCard(new Card("card1", CardPriority.I_NEED_TO_DO_IT_TODAY_OR_TOMORROW));
        cardRegistry.createCard(new Card("card2", CardPriority.LEAVE_EVERYTHING_AND_START_WORKING_ON_IT));
        cardRegistry.createCard(new Card("card3", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY));
        cardRegistry.createCard(new Card("card4", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY));
        // act
        final List<Card> actualCardsList = cardRegistry.getPrioritizedCards();
        // assert
        Assert.assertEquals(4, actualCardsList.size());
        Assert.assertEquals("card2", actualCardsList.get(0).getTitle());
        Assert.assertEquals("card3", actualCardsList.get(1).getTitle());
        Assert.assertEquals("card4", actualCardsList.get(2).getTitle());
        Assert.assertEquals("card1", actualCardsList.get(3).getTitle());
    }

    @Test
    public void shouldReturnAllCreatedCards() throws IncorrectCardTitleException {
        // arrange
        final List<Card> expectedCards = List.of(
                new Card("card1", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY),
                new Card("card2", CardPriority.I_NEED_TO_DO_IT_TODAY_OR_TOMORROW)
        );
        expectedCards.forEach(card -> {
            try {
                cardRegistry.createCard(card);
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
        });
        // act
        final Set<Card> actualCards = cardRegistry.getAllCards();
        // assert
        Assert.assertEquals(expectedCards.size(), actualCards.size());

        expectedCards.forEach(card -> Assert.assertTrue(actualCards.stream().anyMatch(card::equals)));
    }

    @Test
    public void shouldReturnEmptyCardsList() {
        // act
        final Set<Card> actualCards = cardRegistry.getAllCards();
        // assert
        Assert.assertTrue(actualCards.isEmpty());
    }

    @Test
    public void shouldNotCreateAlreadyExistingCard() throws IncorrectCardTitleException {
        // arrange
        final Card card = new Card("card1", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY);
        try {
            cardRegistry.createCard(card);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        // act
        try {
            cardRegistry.createCard(card);
            // assert
            Assert.fail("An error should be thrown.");
        } catch (CardAlreadyExistsException e) {
            // do nothing
        }
    }

    @Test
    public void shouldUpdateCard() throws IncorrectCardTitleException {
        // arrange
        Card cardToUpdate = new Card("card1", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY);
        try {
            cardRegistry.createCard(cardToUpdate);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        final Card updatedCard = new Card(cardToUpdate.getTitle(), CardPriority.LEAVE_EVERYTHING_AND_START_WORKING_ON_IT);
        // act
        try {
            cardRegistry.updateCard(updatedCard);
        } catch (NotExistingCardException e) {
            Assert.fail(e.getMessage());
        }
        // assert
        try {
            Card actualCard = cardRegistry.getCard(cardToUpdate.getTitle());
            Assert.assertEquals(updatedCard, actualCard);
        } catch (NotExistingCardException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void shouldNotUpdateNonExistingCard() throws IncorrectCardTitleException {
        // arrange
        final Card updatedCard = new Card("any title", CardPriority.LEAVE_EVERYTHING_AND_START_WORKING_ON_IT);
        // act
        try {
            cardRegistry.updateCard(updatedCard);
            // assert
            Assert.fail("An error should be thrown.");
        } catch (NotExistingCardException e) {
            // do nothing
        }
    }

    @Test
    public void shouldKeepTheMostBottomPriority() throws IncorrectCardTitleException {
        // arrange
        Card cardToUpdate = new Card("card1", cardRegistry.getInitialCardPriority());
        try {
            cardRegistry.createCard(cardToUpdate);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        // act
        try {
            cardRegistry.bottomCardPriority(cardToUpdate.getTitle());
        } catch (NotExistingCardException e) {
            Assert.fail(e.getMessage());
        }
        // assert
        try {
            Card actualCard = cardRegistry.getCard(cardToUpdate.getTitle());
            Assert.assertEquals(cardToUpdate, actualCard);
        } catch (NotExistingCardException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void shouldKeepTheMostImportantPriority() throws IncorrectCardTitleException {
        // arrange
        Card cardToUpdate = new Card("card1", cardRegistry.getMaxCardPriority());
        try {
            cardRegistry.createCard(cardToUpdate);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        // act
        try {
            cardRegistry.increaseCardPriority(cardToUpdate.getTitle());
        } catch (NotExistingCardException e) {
            Assert.fail(e.getMessage());
        }
        // assert
        try {
            Card actualCard = cardRegistry.getCard(cardToUpdate.getTitle());
            Assert.assertEquals(cardToUpdate, actualCard);
        } catch (NotExistingCardException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void shouldRemoveCard() throws IncorrectCardTitleException {
        // arrange
        final Card cardToRemove = new Card("card2", CardPriority.I_NEED_TO_DO_IT_TODAY_OR_TOMORROW);
        final List<Card> expectedCards = List.of(
                new Card("card1", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY),
                cardToRemove
        );
        expectedCards.forEach(card -> {
            try {
                cardRegistry.createCard(card);
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
        });
        // act
        cardRegistry.removeCard(cardToRemove.getTitle());
        // assert
        final Set<Card> actualCards = cardRegistry.getAllCards();
        Assert.assertEquals(expectedCards.size() - 1, actualCards.size());

        Assert.assertTrue(actualCards.stream().noneMatch(cardToRemove::equals));
    }

    @Test
    public void shouldRemoveNonExistingCard() throws IncorrectCardTitleException {
        // arrange
        final Card cardToRemove = new Card("card2", CardPriority.I_NEED_TO_DO_IT_TODAY_OR_TOMORROW);
        final List<Card> createdCards = List.of(
                new Card("card1", CardPriority.WOW_I_NEEDED_TO_DO_IT_YESTERDAY)
        );
        createdCards.forEach(card -> {
            try {
                cardRegistry.createCard(card);
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
        });
        // act
        cardRegistry.removeCard(cardToRemove.getTitle());
        // assert
        final Set<Card> actualCards = cardRegistry.getAllCards();
        Assert.assertEquals(createdCards.size(), actualCards.size());

        Assert.assertTrue(actualCards.stream().noneMatch(cardToRemove::equals));
    }
}
