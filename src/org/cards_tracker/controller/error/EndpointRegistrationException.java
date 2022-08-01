package org.cards_tracker.controller.error;

import org.eclipse.jetty.http.HttpMethod;
import org.jetbrains.annotations.NotNull;

public class EndpointRegistrationException extends Exception {
    public EndpointRegistrationException(@NotNull final String endpointPath,
                                         @NotNull final HttpMethod httpMethod,
                                         @NotNull final Exception cause) {
        super(
                "An endpoint [" + httpMethod.asString() + "] "
                        + endpointPath + " was not registered successfully because of "
                        + cause.getMessage()
        );
    }
}
