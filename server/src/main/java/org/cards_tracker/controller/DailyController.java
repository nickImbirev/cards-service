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
import org.cards_tracker.error.CardAlreadyExistsException;
import org.cards_tracker.error.NotExistingCardException;
import org.cards_tracker.service.TodayCardsService;
import org.eclipse.jetty.http.HttpMethod;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DailyController {

    private static final Logger log = LoggerFactory.getLogger(DailyController.class);

    public static void registerGetCardsEndpoint(@NotNull final Javalin app,
                                                @NotNull final ObjectMapper objectMapper,
                                                @NotNull final TodayCardsService todayCardsService)
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
                final List<String> cardsForToday = todayCardsService.getCardsForToday();
                try {
                    final Cards responseBody = new Cards(cardsForToday);
                    log.debug("Get today cards response body: " + responseBody + ".");
                    ctx.json(responseBody);
                } catch (Exception e) {
                    log.error("Get today cards request was not successful because of: " + e.getMessage() + ".");
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

    public static void registerReshuffleCardsEndpoint(@NotNull final Javalin app,
                                                      @NotNull final ObjectMapper objectMapper,
                                                      @NotNull final TodayCardsService todayCardsService)
            throws EndpointRegistrationException {
        final OpenApiDocumentation apiDocumentation = OpenApiBuilder
                .document()
                .operation(operation -> {
                    operation.description("Update today cards order.");
                })
                .body(Cards.class)
                .result(String.valueOf(HttpCode.BAD_REQUEST.getStatus()), ErrorDto.class)
                .result(String.valueOf(HttpCode.INTERNAL_SERVER_ERROR.getStatus()), ErrorDto.class)
                .result(String.valueOf(HttpCode.OK.getStatus()));
        final String path = "/today/cards";
        try {
            app.put(path, OpenApiBuilder.documented(apiDocumentation, ctx -> {
                log.debug("Reshuffle today cards request has been triggered.");
                final Cards orderedCards;
                try {
                    orderedCards = ctx.bodyAsClass(Cards.class);
                } catch (Exception e) {
                    log.debug("Reshuffle today cards request body was incorrect because of: " + e.getMessage());
                    ctx
                            .status(HttpCode.BAD_REQUEST)
                            .result(objectMapper.writeValueAsBytes(new ErrorDto(e.getMessage())));
                    return;
                }
                log.debug("Reshuffle today cards request body: " + orderedCards + ".");
                try {
                    todayCardsService.reshuffleTodayCards(orderedCards.getCards());
                } catch (CardAlreadyExistsException | NotExistingCardException e) {
                    log.debug("Reshuffling of today cards was not completed because of: " + e.getMessage() + ".");
                    ctx
                            .status(HttpCode.BAD_REQUEST)
                            .result(objectMapper.writeValueAsBytes(new ErrorDto(e.getMessage())));
                    return;
                }
                log.debug("Reshuffling of today cards was completed.");
                ctx.status(HttpCode.OK);
                log.info("Reshuffle today cards request was successful.");
            }));
        } catch (Exception e) {
            throw new EndpointRegistrationException(path, HttpMethod.PUT, e);
        }
    }

    public static void registerCompleteCardEndpoint(@NotNull final Javalin app,
                                                    @NotNull final ObjectMapper objectMapper,
                                                    @NotNull final TodayCardsService todayCardsService)
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
                    todayCardsService.completeCardForToday(cardTitle);
                } catch (NotExistingCardException e) {
                    log.debug("Today card: " + cardTitle + " was not completed because of: " + e.getMessage() + ".");
                    ctx
                            .status(HttpCode.NOT_FOUND)
                            .result(objectMapper.writeValueAsBytes(new ErrorDto(e.getMessage())));
                    return;
                }
                log.debug("Today card: " + cardTitle + " was completed.");
                ctx.status(HttpCode.NO_CONTENT);
                log.info("Complete today card request for card: " + cardTitle + " was successful.");
            }));
        } catch (Exception e) {
            throw new EndpointRegistrationException(path, HttpMethod.DELETE, e);
        }
    }

    public static void registerAddAdditionalCardEndpoint(@NotNull final Javalin app,
                                                         @NotNull final ObjectMapper objectMapper,
                                                         @NotNull final TodayCardsService todayCardsService)
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
                log.debug("Add additional card for today request has been triggered.");
                final Card cardToAdd;
                try {
                    cardToAdd = ctx.bodyAsClass(Card.class);
                } catch (Exception e) {
                    log.debug("Add additional card for today request body was incorrect because of: " + e.getMessage());
                    ctx
                            .status(HttpCode.BAD_REQUEST)
                            .result(objectMapper.writeValueAsBytes(new ErrorDto(e.getMessage())));
                    return;
                }
                log.debug("Add additional card for today request body: " + cardToAdd + ".");
                final String cardTitle = cardToAdd.getTitle();
                try {
                    todayCardsService.addAdditionalCardForToday(cardTitle);
                } catch (NotExistingCardException e) {
                    log.debug("An additional card: " + cardTitle + " was not added for today because of: " + e.getMessage() + ".");
                    ctx
                            .status(HttpCode.NOT_FOUND)
                            .result(objectMapper.writeValueAsBytes(new ErrorDto(e.getMessage())));
                    return;
                }
                log.debug("An additional card: " + cardTitle + " was added for today.");
                ctx.status(HttpCode.CREATED);
                log.info("Add additional card for today request for card: " + cardTitle + " was successful.");
            }));
        } catch (Exception e) {
            throw new EndpointRegistrationException(path, HttpMethod.POST, e);
        }
    }
}
