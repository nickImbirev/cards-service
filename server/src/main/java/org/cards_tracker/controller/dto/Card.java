package org.cards_tracker.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cards_tracker.error.IncorrectCardTitleException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Card {
    @NotNull
    private final String title;
    @Nullable
    private final PriorityUpdateSchedule priorityUpdateSchedule;

    @JsonCreator
    public Card(
            @JsonProperty(value = "title", required = true) @NotNull(exception = IncorrectCardTitleException.class) final String title,
            @JsonProperty(value = "priorityUpdateSchedule") @Nullable final PriorityUpdateSchedule priorityUpdateSchedule
    ) throws IncorrectCardTitleException {
        if (!isTitleValid(title)) throw new IncorrectCardTitleException(title);
        this.title = title;
        this.priorityUpdateSchedule = priorityUpdateSchedule;
    }

    private boolean isTitleValid(@NotNull final String title) {
        return title.trim().length() != 0;
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    @Nullable
    public PriorityUpdateSchedule getPriorityUpdateSchedule() {
        return priorityUpdateSchedule;
    }

    @Override
    public String toString() {
        return "Card{" +
                "title='" + title + '\'' +
                ", priorityUpdateSchedule=" + priorityUpdateSchedule +
                '}';
    }
}
