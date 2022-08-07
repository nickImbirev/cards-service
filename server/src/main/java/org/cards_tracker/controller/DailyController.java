package org.cards_tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.HttpCode;
import io.javalin.plugin.openapi.dsl.OpenApiBuilder;
import io.javalin.plugin.openapi.dsl.OpenApiDocumentation;
import org.cards_tracker.controller.dto.Card;
import org.cards_tracker.controller.dto.Cards;
import org.cards_tracker.controller.dto.ErrorDto;
import org.cards_tracker.controller.error.EndpointRegistrationException;
import org.cards_tracker.error.NotExistingCardException;
import org.cards_tracker.service.CardService;
import org.eclipse.jetty.http.HttpMethod;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DailyController {

    public static void registerGetCardsEndpoint(@NotNull final Javalin app,
                                                @NotNull final ObjectMapper objectMapper,
                                                @NotNull final CardService cardService)
            throws EndpointRegistrationException {
        final OpenApiDocumentation apiDocumentation = OpenApiBuilder
                .document()
                .operation(operation -> {
                    operation.description("Get cards for today.");
                })
                .result(String.valueOf(HttpCode.INTERNAL_SERVER_ERROR.getStatus()), ErrorDto.class)
                .result(String.valueOf(HttpCode.OK.getStatus()), Cards.class);
        final String path = "/today/cards";
        try {
            app.get(path, OpenApiBuilder.documented(apiDocumentation, ctx -> {
                final List<String> cardsForToday = cardService.getCardsForToday();
                try {
                    ctx.json(new Cards(cardsForToday));
                } catch (Exception e) {
                    ctx
                            .status(HttpCode.INTERNAL_SERVER_ERROR)
                            .result(objectMapper.writeValueAsBytes(new ErrorDto(e.getMessage())));
                    return;
                }
                ctx.status(HttpCode.OK);
            }));
        } catch (Exception e) {
            throw new EndpointRegistrationException(path, HttpMethod.GET, e);
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
                .result(String.valueOf(HttpCode.NO_CONTENT.getStatus()));
        final String path = "/today/card";
        try {
            app.delete(path, OpenApiBuilder.documented(apiDocumentation, ctx -> {
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
                } catch (NotExistingCardException e) {
                    ctx
                            .status(HttpCode.NOT_FOUND)
                            .result(objectMapper.writeValueAsBytes(new ErrorDto(e.getMessage())));
                    return;
                }
                ctx.status(HttpCode.NO_CONTENT);
            }));
        } catch (Exception e) {
            throw new EndpointRegistrationException(path, HttpMethod.DELETE, e);
        }
    }

    public static void registerAddAdditionalCardEndpoint(@NotNull final Javalin app,
                                                         @NotNull final ObjectMapper objectMapper,
                                                         @NotNull final CardService cardService)
            throws EndpointRegistrationException {
        final OpenApiDocumentation apiDocumentation = OpenApiBuilder
                .document()
                .operation(operation -> {
                    operation.description("Add an additional card for today.");
                })
                .body(Card.class)
                .json(String.valueOf(HttpCode.BAD_REQUEST.getStatus()), ErrorDto.class)
                .json(String.valueOf(HttpCode.NOT_FOUND.getStatus()), ErrorDto.class)
                .result(String.valueOf(HttpCode.CREATED.getStatus()));
        final String path = "/today/card";
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
                    cardService.addAdditionalCardForToday(cardToCreate.getTitle());
                } catch (NotExistingCardException e) {
                    ctx
                            .status(HttpCode.NOT_FOUND)
                            .result(objectMapper.writeValueAsBytes(new ErrorDto(e.getMessage())));
                    return;
                }
                ctx.status(HttpCode.CREATED);
            }));
        } catch (Exception e) {
            throw new EndpointRegistrationException(path, HttpMethod.POST, e);
        }
    }
}
