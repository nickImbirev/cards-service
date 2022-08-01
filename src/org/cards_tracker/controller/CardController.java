package org.cards_tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.HttpCode;
import org.cards_tracker.controller.dto.Card;
import org.cards_tracker.controller.dto.ErrorDto;
import org.cards_tracker.controller.error.EndpointRegistrationException;
import org.cards_tracker.error.CardAlreadyExistsException;
import org.cards_tracker.error.IncorrectCardTitleException;
import org.cards_tracker.service.CardService;
import org.eclipse.jetty.http.HttpMethod;
import org.jetbrains.annotations.NotNull;

public class CardController {
    public static void registerCreateCardEndpoint(@NotNull final Javalin app,
                                                  @NotNull final ObjectMapper objectMapper,
                                                  @NotNull final CardService cardService) throws EndpointRegistrationException {
        final String path = "/card";
        try {
            app.post(path, ctx -> {
                final Card cardToCreate;
                try {
                    cardToCreate = ctx.bodyAsClass(Card.class);
                } catch (Exception e) {
                    ctx
                            .status(HttpCode.BAD_REQUEST)
                            .result(objectMapper.writeValueAsBytes(new ErrorDto(e.getMessage())));
                    return;
                }
                try {
                    cardService.createCard(cardToCreate.getTitle());
                } catch (IncorrectCardTitleException | CardAlreadyExistsException e) {
                    ctx
                            .status(HttpCode.BAD_REQUEST)
                            .result(objectMapper.writeValueAsBytes(new ErrorDto(e.getMessage())));
                    return;
                }
                ctx.status(HttpCode.CREATED);
            });
        } catch (Exception e) {
            throw new EndpointRegistrationException(path, HttpMethod.POST, e);
        }
    }
}
