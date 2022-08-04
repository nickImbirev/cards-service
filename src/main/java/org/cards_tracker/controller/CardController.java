package org.cards_tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.HttpCode;
import io.javalin.plugin.openapi.dsl.OpenApiBuilder;
import io.javalin.plugin.openapi.dsl.OpenApiDocumentation;
import org.cards_tracker.controller.dto.Card;
import org.cards_tracker.controller.dto.ErrorDto;
import org.cards_tracker.controller.error.EndpointRegistrationException;
import org.cards_tracker.error.CardAlreadyExistsException;
import org.cards_tracker.error.IncorrectCardTitleException;
import org.cards_tracker.error.NotExistingCardException;
import org.cards_tracker.service.CardService;
import org.eclipse.jetty.http.HttpMethod;
import org.jetbrains.annotations.NotNull;

public class CardController {
    public static void registerCreateCardEndpoint(@NotNull final Javalin app,
                                                  @NotNull final ObjectMapper objectMapper,
                                                  @NotNull final CardService cardService) throws EndpointRegistrationException {
        final OpenApiDocumentation apiDocumentation = OpenApiBuilder
                .document()
                .operation(operation -> {
                    operation.description("Create brand new card (task).");
                })
                .body(Card.class)
                .json(String.valueOf(HttpCode.BAD_REQUEST.getStatus()), ErrorDto.class)
                .result(String.valueOf(HttpCode.CREATED.getStatus()));
        final String path = "/card";
        try {
            app.post(path, OpenApiBuilder.documented(apiDocumentation, ctx -> {
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
            }));
        } catch (Exception e) {
            throw new EndpointRegistrationException(path, HttpMethod.POST, e);
        }
    }

    public static void registerCompleteCardEndpoint(@NotNull final Javalin app,
                                                    @NotNull final ObjectMapper objectMapper,
                                                    @NotNull final CardService cardService)
            throws EndpointRegistrationException {
        final OpenApiDocumentation apiDocumentation = OpenApiBuilder
                .document()
                .operation(operation -> {
                    operation.description("Complete the specified card for today.");
                })
                .body(Card.class)
                .json(String.valueOf(HttpCode.BAD_REQUEST.getStatus()), ErrorDto.class)
                .json(String.valueOf(HttpCode.NOT_FOUND.getStatus()), ErrorDto.class)
                .result(String.valueOf(HttpCode.OK.getStatus()));
        final String path = "/card";
        try {
            app.put(path, OpenApiBuilder.documented(apiDocumentation, ctx -> {
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
                    cardService.completeCardForToday(cardToCreate.getTitle());
                } catch (IncorrectCardTitleException e) {
                    ctx
                            .status(HttpCode.BAD_REQUEST)
                            .result(objectMapper.writeValueAsBytes(new ErrorDto(e.getMessage())));
                    return;
                } catch (NotExistingCardException e) {
                    ctx
                            .status(HttpCode.NOT_FOUND)
                            .result(objectMapper.writeValueAsBytes(new ErrorDto(e.getMessage())));
                    return;
                }
                ctx.status(HttpCode.OK);
            }));
        } catch (Exception e) {
            throw new EndpointRegistrationException(path, HttpMethod.PUT, e);
        }
    }
}
