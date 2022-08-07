package org.cards_tracker.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Cards {
    @NotNull
    private final List<String> cards;

    @JsonCreator
    public Cards(@JsonProperty(value = "cards", required = true) @NotNull final List<String> cards) {
        this.cards = cards;
    }

    @SuppressWarnings("unused")
    @NotNull
    @JsonProperty("cards")
    public List<String> getCards() {
        return cards;
    }
}
