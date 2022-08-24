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
import org.cards_tracker.error.IncorrectCardTitleException;
import org.cards_tracker.error.NotExistingCardException;
import org.cards_tracker.service.CardService;
import org.eclipse.jetty.http.HttpMethod;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DailyController {

    private static final Logger log = LoggerFactory.getLogger(DailyController.class);

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
                log.debug("Get today cards request has been triggered.");
                final List<String> cardsForToday = cardService.getCardsForToday();
                try {
                    final Cards responseBody = new Cards(cardsForToday);
                    log.debug("Get today cards response body: " + responseBody + ".");
                    ctx.json(responseBody);
                } catch (Exception e) {
                    log.debug("Get today cards request was not successful because of: " + e.getMessage() + ".");
                    ctx
                            .status(HttpCode.INTERNAL_SERVER_ERROR)
                            .result(objectMapper.writeValueAsBytes(new ErrorDto(e.getMessage())));
                    return;
                }
                ctx.status(HttpCode.OK);
                log.info("Get today cards request was successful.");
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
                .result(String.valueOf(HttpCode.OK.getStatus()));
        final String path = "/today/card";
        try {
            app.put(path, OpenApiBuilder.documented(apiDocumentation, ctx -> {
                log.debug("Complete today card request has been triggered.");
                final Card cardToCreate;
                try {
                    cardToCreate = ctx.bodyAsClass(Card.class);
                } catch (Exception e) {
                    log.debug("Complete today card request body was incorrect because of: " + e.getMessage());
                    ctx
                            .status(HttpCode.BAD_REQUEST)
                            .result(objectMapper.writeValueAsBytes(new ErrorDto(e.getMessage())));
                    return;
                }
                log.debug("Complete today card request body: " + cardToCreate + ".");
                final String cardTitle = cardToCreate.getTitle();
                try {
                    cardService.completeCardForToday(cardTitle);
                } catch (IncorrectCardTitleException e) {
                    log.debug("Today card: " + cardTitle + " was not completed because of: " + e.getMessage() + ".");
                    ctx
                            .status(HttpCode.BAD_REQUEST)
                            .result(objectMapper.writeValueAsBytes(new ErrorDto(e.getMessage())));
                    return;
                } catch (NotExistingCardException e) {
                    log.debug("Today card: " + cardTitle + " was not completed because of: " + e.getMessage() + ".");
                    ctx
                            .status(HttpCode.NOT_FOUND)
                            .result(objectMapper.writeValueAsBytes(new ErrorDto(e.getMessage())));
                    return;
                }
                log.debug("Today card: " + cardTitle + " was completed.");
                ctx.status(HttpCode.OK);
                log.info("Complete today card request for card: " + cardTitle + " was successful.");
            }));
        } catch (Exception e) {
            throw new EndpointRegistrationException(path, HttpMethod.PUT, e);
        }
    }
}
