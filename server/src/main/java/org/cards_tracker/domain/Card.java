package org.cards_tracker.domain;

import org.cards_tracker.error.IncorrectCardTitleException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Card {
    @NotNull
    private final String title;
    @NotNull
    private final CardPriority cardPriority;

    public Card(@NotNull final String title, @NotNull final CardPriority cardPriority) throws IncorrectCardTitleException {
        if (isTitleInvalid(title)) {
            throw new IncorrectCardTitleException(title);
        }
        this.title = title;
        this.cardPriority = cardPriority;
    }

    private boolean isTitleInvalid(@NotNull final String title) {
        return title.trim().length() == 0;
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    @NotNull
    public CardPriority getCardPriority() {
        return cardPriority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return title.equals(card.title) && cardPriority == card.cardPriority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, cardPriority);
    }

    @Override
    public String toString() {
        return "Card{" +
                "title='" + title + '\'' +
                ", cardPriority=" + cardPriority +
                '}';
    }
}
