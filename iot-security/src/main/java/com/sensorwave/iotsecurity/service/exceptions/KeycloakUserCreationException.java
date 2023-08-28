package com.sensorwave.iotsecurity.service.exceptions;

import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.openapi.quarkus.keycloak_api_yaml.model.UserRepresentation;

public class KeycloakUserCreationException extends ClientWebApplicationExceptionWrapper {

    private final UserRepresentation userRepresentation;

    public KeycloakUserCreationException(
        final ClientWebApplicationException e,
        final UserRepresentation userRepresentation
    ) {
        super(e);
        this.userRepresentation = userRepresentation;
    }

    public UserRepresentation getUserRepresentation() {
        return userRepresentation;
    }
}
