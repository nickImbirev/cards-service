package org.cards_tracker.error;

import org.jetbrains.annotations.NotNull;

public class CardAlreadyExistsException extends Exception {
    public CardAlreadyExistsException(@NotNull String title) {
        super("Card with title: " + title + " already exists");
    }
}
