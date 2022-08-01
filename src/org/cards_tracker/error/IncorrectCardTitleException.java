package org.cards_tracker.error;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IncorrectCardTitleException extends Exception {
    public IncorrectCardTitleException(@Nullable String title) {
        super("The card title " + (title != null ? title : "") + " is incorrect");
    }

    public IncorrectCardTitleException(@Nullable String message, @NotNull Throwable cause) {
        super(message != null ? message : "The card title is incorrect", cause);
    }
}
