package org.cards_tracker.service;

import org.cards_tracker.error.CardAlreadyExistsException;
import org.cards_tracker.error.IncorrectCardTitleException;
import org.cards_tracker.error.NotExistingCardException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CardRegistry {

    void createCard(@NotNull final String title) throws IncorrectCardTitleException, CardAlreadyExistsException;

    boolean isCardExist(@NotNull final String title);

    @NotNull
    List<String> getPrioritizedCards();

    void bottomCardPriority(@NotNull final String title) throws NotExistingCardException;

    void removeCard(@NotNull final String title);
}
