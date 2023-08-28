package com.sensorwave.iotsecurity.service.exceptions;

import org.jboss.resteasy.reactive.ClientWebApplicationException;

public class KeycloakAdminTokenGenerationException extends ClientWebApplicationExceptionWrapper {
    public KeycloakAdminTokenGenerationException(ClientWebApplicationException clientWebAppException) {
        super(clientWebAppException);
    }
}
