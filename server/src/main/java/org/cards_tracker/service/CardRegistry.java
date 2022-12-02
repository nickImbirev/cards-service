package org.cards_tracker.service;

import org.cards_tracker.domain.Card;
import org.cards_tracker.domain.CardPriority;
import org.cards_tracker.error.CardAlreadyExistsException;
import org.cards_tracker.error.NotExistingCardException;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public interface CardRegistry {

    void createCard(@NotNull final Card card) throws CardAlreadyExistsException;

    boolean isCardExist(@NotNull final String title);

    @NotNull
    Card getCard(@NotNull final String title) throws NotExistingCardException;

    @NotNull
    Set<Card> getAllCards();

    @NotNull
    List<Card> getPrioritizedCards();

    @NotNull
    CardPriority getInitialCardPriority();

    @NotNull
    CardPriority getMaxCardPriority();

    void updateCard(@NotNull final Card updatedCard) throws NotExistingCardException;

    void updateCardPriority(@NotNull final String title, @NotNull final CardPriority updatedPriority) throws NotExistingCardException;

    void bottomCardPriority(@NotNull final String title) throws NotExistingCardException;

    void increaseCardPriority(@NotNull final String title) throws NotExistingCardException;

    void removeCard(@NotNull final String title);
}
