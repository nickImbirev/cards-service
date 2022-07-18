package org.cards_tracker.error;

import org.jetbrains.annotations.NotNull;

public class IncorrectCardTitleException extends Exception {
    public IncorrectCardTitleException(@NotNull String title) {
        super("The title: " + title + " is incorrect");
    }
}
