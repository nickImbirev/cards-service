package org.cards_tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.HttpCode;
import io.javalin.plugin.openapi.dsl.OpenApiBuilder;
import io.javalin.plugin.openapi.dsl.OpenApiDocumentation;
import org.cards_tracker.controller.dto.Card;
import org.cards_tracker.controller.dto.PriorityUpdateSchedule;
import org.cards_tracker.controller.dto.ErrorDto;
import org.cards_tracker.controller.error.EndpointRegistrationException;
import org.cards_tracker.domain.CardPriority;
import org.cards_tracker.domain.CardPriorityUpdateSchedule;
import org.cards_tracker.error.CardAlreadyExistsException;
import org.cards_tracker.error.IncorrectCardPriorityScheduleException;
import org.cards_tracker.error.IncorrectCardTitleException;
import org.cards_tracker.error.NotExistingCardException;
import org.cards_tracker.service.CardRegistry;
import org.cards_tracker.service.CardsUpdateScheduler;
import org.cards_tracker.service.TodayCardsService;
import org.eclipse.jetty.http.HttpMethod;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CardController {

    private static final Logger log = LoggerFactory.getLogger(CardController.class);

    public static void registerCreateScheduledCardEndpoint(@NotNull final Javalin app,
                                                           @NotNull final ObjectMapper objectMapper,
                                                           @NotNull final CardRegistry cardRegistry,
                                                           @NotNull final CardsUpdateScheduler priorityUpdateScheduler)
            throws EndpointRegistrationException {
        final OpenApiDocumentation apiDocumentation = OpenApiBuilder
                .document()
                .operation(operation -> {
                    operation.description("Create brand new scheduled card (task).");
                })
                .body(Card.class)
                .json(String.valueOf(HttpCode.BAD_REQUEST.getStatus()), ErrorDto.class)
                .json(String.valueOf(HttpCode.INTERNAL_SERVER_ERROR.getStatus()), ErrorDto.class)
                .result(String.valueOf(HttpCode.CREATED.getStatus()));
        final String path = "/card";
        try {
            app.post(path, OpenApiBuilder.documented(apiDocumentation, ctx -> {
                log.debug("Create scheduled card request has been triggered.");
                final Card cardToCreate;
                try {
                    cardToCreate = ctx.bodyAsClass(Card.class);
                } catch (Exception e) {
                    log.debug("Create card request body was incorrect because of: " + e.getMessage());
                    ctx
                            .status(HttpCode.BAD_REQUEST)
                            .result(objectMapper.writeValueAsBytes(new ErrorDto(e.getMessage())));
                    return;
                }
                log.debug("Create scheduled card request body: " + cardToCreate + ".");
                String cardTitle = cardToCreate.getTitle();
                final CardPriority initialCardPriority = cardRegistry.getInitialCardPriority();
                try {
                    cardRegistry.createCard(new org.cards_tracker.domain.Card(cardTitle, initialCardPriority));
                } catch (IncorrectCardTitleException e) {
                    log.debug("Card: " + cardTitle + " was not created because of: " + e.getMessage() + ".");
                    ctx
                            .status(HttpCode.BAD_REQUEST)
                            .result(objectMapper.writeValueAsBytes(new ErrorDto(e.getMessage())));
                    return;
                } catch (CardAlreadyExistsException e) {
                    log.error("Card: " + cardTitle + " was not created because of: " + e.getMessage() + ".");
                    ctx
                            .status(HttpCode.INTERNAL_SERVER_ERROR)
                            .result(objectMapper.writeValueAsBytes(new ErrorDto(e.getMessage())));
                    return;
                }
                log.debug("Card: " + cardTitle + " was created.");
                final PriorityUpdateSchedule priorityUpdateSchedule = cardToCreate.getPriorityUpdateSchedule();
                if (priorityUpdateSchedule != null) {
                    try {
                        priorityUpdateScheduler.schedulePriorityUpdate(
                                cardTitle,
                                new CardPriorityUpdateSchedule(priorityUpdateSchedule.getSdkTimeUnit(), priorityUpdateSchedule.getPeriod())
                        );
                    } catch (NotExistingCardException | IncorrectCardPriorityScheduleException | IllegalArgumentException e) {
                        log.error("Card: " + cardTitle + " next priority update was not scheduled because of: " + e.getMessage() + ".");
                        ctx
                                .status(HttpCode.INTERNAL_SERVER_ERROR)
                                .result(objectMapper.writeValueAsBytes(new ErrorDto(e.getMessage())));
                        return;
                    }
                } else {
                    try {
                        priorityUpdateScheduler.scheduleDefaultPriorityUpdate(cardTitle);
                    } catch (NotExistingCardException e) {
                        log.error("Card: " + cardTitle + " next priority update was not scheduled because of: " + e.getMessage() + ".");
                        ctx
                                .status(HttpCode.INTERNAL_SERVER_ERROR)
                                .result(objectMapper.writeValueAsBytes(new ErrorDto(e.getMessage())));
                        return;
                    }
                }
                log.debug("Card: " + cardTitle + " next priority update was scheduled.");
                ctx.status(HttpCode.CREATED);
                log.info("Create card request for card: " + cardTitle + " was successful.");
            }));
        } catch (Exception e) {
            throw new EndpointRegistrationException(path, HttpMethod.POST, e);
        }
    }

    public static void registerDeleteCardEndpoint(@NotNull final Javalin app,
                                                  @NotNull final ObjectMapper objectMapper,
                                                  @NotNull final CardRegistry cardRegistry,
                                                  @NotNull final TodayCardsService todayCardsService)
            throws EndpointRegistrationException {
        final OpenApiDocumentation apiDocumentation = OpenApiBuilder
                .document()
                .operation(operation -> {
                    operation.description("Delete the specified card completely or do nothing in case it did not exist before.");
                })
                .body(Card.class)
                .json(String.valueOf(HttpCode.BAD_REQUEST.getStatus()), ErrorDto.class)
                .result(String.valueOf(HttpCode.NO_CONTENT.getStatus()));
        final String path = "/card";
        try {
            app.delete(path, OpenApiBuilder.documented(apiDocumentation, ctx -> {
                log.debug("Delete card request has been triggered.");
                final Card cardToCreate;
                try {
                    cardToCreate = ctx.bodyAsClass(Card.class);
                } catch (Exception e) {
                    log.debug("Delete card request body was incorrect because of: " + e.getMessage() + ".");
                    ctx
                            .status(HttpCode.BAD_REQUEST)
                            .result(objectMapper.writeValueAsBytes(new ErrorDto(e.getMessage())));
                    return;
                }
                String cardTitle = cardToCreate.getTitle();
                try {
                    todayCardsService.completeCardForToday(cardTitle);
                    log.debug("Today card: " + cardTitle + " was completed.");
                } catch (NotExistingCardException e) {
                    log.debug("Today card: " + cardTitle + " was not completed because of: " + e.getMessage() + ".");
                }
                cardRegistry.removeCard(cardTitle);
                log.debug("Card: " + cardTitle + " was deleted.");
                ctx.status(HttpCode.NO_CONTENT);
                log.info("Delete card request for card: " + cardTitle + " was successful.");
            }));
        } catch (Exception e) {
            throw new EndpointRegistrationException(path, HttpMethod.DELETE, e);
        }
    }
}
