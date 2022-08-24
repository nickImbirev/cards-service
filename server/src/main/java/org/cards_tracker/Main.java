package org.cards_tracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.info.Info;
import org.cards_tracker.controller.CardController;
import org.cards_tracker.controller.DailyController;
import org.cards_tracker.controller.error.EndpointRegistrationException;
import org.cards_tracker.service.CardService;
import org.cards_tracker.service.ScheduledInMemoryCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final int MAX_CARDS_FOR_TODAY_DEFAULT = 5;
    private static final long PERIOD_DEFAULT = 1L;
    private static final TimeUnit TIME_UNIT_DEFAULT = TimeUnit.MINUTES;


    public static void main(String[] args) {
        int maxCardsForToday;
        try {
            int cardsArgIndex = 0;
            if (args.length <= cardsArgIndex || args[cardsArgIndex] == null) {
                log.debug(
                        "Cards number argument was not provided, default value: "
                                + MAX_CARDS_FOR_TODAY_DEFAULT + " will be used."
                );
                maxCardsForToday = MAX_CARDS_FOR_TODAY_DEFAULT;
            } else {
                maxCardsForToday = Integer.parseInt(args[cardsArgIndex]);
                if (maxCardsForToday <= 0) {
                    log.warn(
                            "Incorrect cards number argument was provided, default value: "
                                    + MAX_CARDS_FOR_TODAY_DEFAULT + " will be used."
                    );
                    maxCardsForToday = MAX_CARDS_FOR_TODAY_DEFAULT;
                } else {
                    log.debug("Cards number argument value is: " + maxCardsForToday);
                }
            }
        } catch (Exception e) {
            log.warn(
                    "Unable to read the cards number argument because of: "
                            + e.getMessage() + ", so, the default value: "
                            + MAX_CARDS_FOR_TODAY_DEFAULT + " will be used."
            );
            maxCardsForToday = MAX_CARDS_FOR_TODAY_DEFAULT;
        }
        long period;
        try {
            int periodArgIndex = 1;
            if (args.length <= periodArgIndex || args[periodArgIndex] == null) {
                log.debug(
                        "Period argument was not provided, default value: "
                                + PERIOD_DEFAULT + " will be used."
                );
                period = PERIOD_DEFAULT;
            } else {
                period = Long.parseLong(args[periodArgIndex]);
                if (period <= 0) {
                    log.warn(
                            "Incorrect period argument was provided, default value: "
                                    + PERIOD_DEFAULT + " will be used."
                    );
                    period = PERIOD_DEFAULT;
                } else {
                    log.debug("Period argument value is: " + period);
                }
            }
        } catch (Exception e) {
            log.warn(
                    "Unable to read the period argument because of: "
                            + e.getMessage() + ", so, the default value: "
                            + PERIOD_DEFAULT + " will be used."
            );
            period = PERIOD_DEFAULT;
        }
        TimeUnit timeUnit;
        try {
            int timeUnitArgIndex = 2;
            if (args.length <= timeUnitArgIndex || args[timeUnitArgIndex] == null) {
                timeUnit = TIME_UNIT_DEFAULT;
                log.debug(
                        "Time unit argument was not provided, default value: "
                                + TIME_UNIT_DEFAULT + " will be used."
                );
            } else {
                timeUnit = TimeUnit.valueOf(args[timeUnitArgIndex]);
                log.debug("Time unit argument value is: " + timeUnit);
            }
        } catch (Exception e) {
            log.warn(
                    "Unable to read the time unit argument because of: "
                            + e.getMessage() + ", so, the default value: "
                            + TIME_UNIT_DEFAULT + " will be used."
            );
            timeUnit = TIME_UNIT_DEFAULT;
        }

        final Javalin app = Javalin.create(config -> {
            config.registerPlugin(
                    new OpenApiPlugin(
                            new OpenApiOptions
                                    (new Info().title("Cards service").version("1.0.0").description("Cards service"))
                                    .swagger(new SwaggerOptions("/swagger-ui").title("Card service API Documentation"))
                                    .path("/swagger-docs")
                    ));
            config.enableCorsForAllOrigins();
            if (log.isDebugEnabled()) {
                config.enableDevLogging();
            }
        }).start(8081);

        final ObjectMapper objectMapper = new ObjectMapper();
        CardService cardService =
                new ScheduledInMemoryCardService(timeUnit, period, maxCardsForToday);

        try {
            CardController.registerCreateCardEndpoint(app, objectMapper, cardService);
            log.debug("Create card API has been registered.");
        } catch (EndpointRegistrationException e) {
            log.warn(e.getMessage());
        }
        try {
            DailyController.registerCompleteCardEndpoint(app, objectMapper, cardService);
            log.debug("Complete today card API has been registered.");
        } catch (EndpointRegistrationException e) {
            log.warn(e.getMessage());
        }
        try {
            CardController.registerDeleteCardEndpoint(app, objectMapper, cardService);
            log.debug("Create card API has been registered.");
        } catch (EndpointRegistrationException e) {
            log.warn(e.getMessage());
        }
        try {
            DailyController.registerGetCardsEndpoint(app, objectMapper, cardService);
            log.debug("Get today cards API has been registered.");
        } catch (EndpointRegistrationException e) {
            log.warn(e.getMessage());
        }
        try {
            DailyController.registerAddAdditionalCardEndpoint(app, objectMapper, cardService);
        } catch (EndpointRegistrationException e) {
            log.warn(e.getMessage());
        }
    }
}
