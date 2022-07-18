package org.cards_tracker.service;

import org.cards_tracker.error.CardAlreadyExistsException;
import org.cards_tracker.error.IncorrectCardTitleException;
import org.cards_tracker.error.NotExistingCardException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CardService {

    void createCard(final @NotNull String title) throws IncorrectCardTitleException, CardAlreadyExistsException;

    @NotNull
    List<String> getCardsForToday();

    void completeCardForToday(final @NotNull String title) throws IncorrectCardTitleException, NotExistingCardException;

    void removeCard(final @NotNull String title) throws IncorrectCardTitleException;
}
