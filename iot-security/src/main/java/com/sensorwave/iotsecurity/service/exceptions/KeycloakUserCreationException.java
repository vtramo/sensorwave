package com.sensorwave.iotsecurity.service.exceptions;

import lombok.Getter;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.openapi.quarkus.keycloak_api_yaml.model.UserRepresentation;

@Getter
public class KeycloakUserCreationException extends ClientWebApplicationExceptionWrapper {

    private final UserRepresentation userRepresentation;

    public KeycloakUserCreationException(
        final ClientWebApplicationException e,
        final UserRepresentation userRepresentation
    ) {
        super(e);
        this.userRepresentation = userRepresentation;
    }

}
