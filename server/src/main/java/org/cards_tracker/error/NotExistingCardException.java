package org.cards_tracker.error;

import org.jetbrains.annotations.NotNull;

public class NotExistingCardException extends Exception {
    public NotExistingCardException(@NotNull String title) {
        super("Card with title: " + title + " does not exist");
    }
}
