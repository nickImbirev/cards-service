package org.cards_tracker.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cards_tracker.error.IncorrectCardTitleException;
import org.jetbrains.annotations.NotNull;

public class Card {
    @NotNull
    private final String title;

    @JsonCreator
    public Card(
            @JsonProperty(value = "title", required = true) @NotNull(exception = IncorrectCardTitleException.class) final String title
    ) throws IncorrectCardTitleException {
        if (!isTitleValid(title)) throw new IncorrectCardTitleException(title);
        this.title = title;
    }

    private boolean isTitleValid(@NotNull final String title) {
        return title.trim().length() != 0;
    }

    @NotNull
    public String getTitle() {
        return title;
    }
}
