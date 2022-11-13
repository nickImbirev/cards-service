package org.cards_tracker.service;

import org.cards_tracker.error.CardAlreadyExistsException;
import org.cards_tracker.error.NotExistingCardException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface TodayCardsService {
    void addAdditionalCardForToday(@NotNull final String title) throws NotExistingCardException;

    @NotNull
    List<String> getCardsForToday();

    void reshuffleTodayCards(@NotNull final List<String> todayCards) throws NotExistingCardException, CardAlreadyExistsException;

    void completeCardForToday(final @NotNull String title) throws NotExistingCardException;
}
