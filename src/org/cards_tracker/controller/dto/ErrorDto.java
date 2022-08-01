package org.cards_tracker.controller.dto;

import org.jetbrains.annotations.NotNull;

public class ErrorDto {
    @NotNull
    private final String details;

    public ErrorDto(@NotNull final String details) {
        this.details = details;
    }

    @NotNull
    public String getDetails() {
        return details;
    }
}
