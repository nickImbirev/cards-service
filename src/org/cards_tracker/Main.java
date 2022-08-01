package org.cards_tracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import org.cards_tracker.controller.CardController;
import org.cards_tracker.controller.error.EndpointRegistrationException;
import org.cards_tracker.service.CardService;
import org.cards_tracker.service.ScheduledInMemoryCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        final Javalin app = Javalin.create().start(8081);
        final ObjectMapper objectMapper = new ObjectMapper();
        CardService cardService =
                new ScheduledInMemoryCardService(TimeUnit.MINUTES, 1L, 5);

        try {
            CardController.registerCreateCardEndpoint(app, objectMapper, cardService);
        } catch (EndpointRegistrationException e) {
            log.warn(e.getMessage());
        }
    }
}
