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

    public static void main(String[] args) {
        final Javalin app = Javalin.create(config -> config.registerPlugin(
                new OpenApiPlugin(
                        new OpenApiOptions
                                (new Info().title("Cards service").version("1.0.0").description("Cards service"))
                                .swagger(new SwaggerOptions("/swagger-ui").title("Card service API Documentation"))
                                .path("/swagger-docs")
                ))).start(8081);

        final ObjectMapper objectMapper = new ObjectMapper();
        CardService cardService =
                new ScheduledInMemoryCardService(TimeUnit.MINUTES, 1L, 5);

        try {
            CardController.registerCreateCardEndpoint(app, objectMapper, cardService);
        } catch (EndpointRegistrationException e) {
            log.warn(e.getMessage());
        }
        try {
            DailyController.registerCompleteCardEndpoint(app, objectMapper, cardService);
        } catch (EndpointRegistrationException e) {
            log.warn(e.getMessage());
        }
        try {
            CardController.registerDeleteCardEndpoint(app, objectMapper, cardService);
        } catch (EndpointRegistrationException e) {
            log.warn(e.getMessage());
        }
        try {
            DailyController.registerGetCardsEndpoint(app, objectMapper, cardService);
        } catch (EndpointRegistrationException e) {
            log.warn(e.getMessage());
        }
    }
}
