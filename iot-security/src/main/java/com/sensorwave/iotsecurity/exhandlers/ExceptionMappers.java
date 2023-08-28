package com.sensorwave.iotsecurity.exhandlers;

import com.sensorwave.iotsecurity.service.exceptions.KeycloakAdminTokenGenerationException;
import com.sensorwave.iotsecurity.service.exceptions.KeycloakUserCreationException;
import io.netty.handler.codec.http.HttpResponseStatus;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.openapi.quarkus.iot_security_api_yaml.model.ErrorResponse;
import org.openapi.quarkus.keycloak_api_yaml.model.UserRepresentation;


public class ExceptionMappers {

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleKeycloakUserCreationException(final KeycloakUserCreationException kcUserCreationException) {
        final Response errorResponse = kcUserCreationException.getResponse();
        final int status = errorResponse.getStatus();
        final UserRepresentation userRepresentation = kcUserCreationException.getUserRepresentation();

        final ErrorResponse error = new ErrorResponse();
        RestResponse.Status responseStatus;
        if (status == HttpResponseStatus.CONFLICT.code()) {
            error.title("The user name already exists.");
            error.message(String.format("The keycloak username %s already exists!", userRepresentation.getUsername()));
            error.status(HttpResponseStatus.CONFLICT.code());
            error.code(ErrorCodes.KC_USER_CREATION_DUP_USERNAME_ERROR_CODE);
            responseStatus = RestResponse.Status.CONFLICT;
        } else if (status == HttpResponseStatus.UNAUTHORIZED.code()) {
            error.title("Unauthorized.");
            error.message("You do not have permission to create a keycloak user!");
            error.status(HttpResponseStatus.UNAUTHORIZED.code());
            error.code(ErrorCodes.KC_USER_CREATION_UNAUTHORIZED_ERROR_CODE);
            responseStatus = RestResponse.Status.UNAUTHORIZED;
        } else {
            error.title("Internal server error.");
            error.message(kcUserCreationException.getMessage());
            error.status(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
            error.code(ErrorCodes.INTERNAL_SERVER_ERROR_CODE);
            responseStatus = RestResponse.Status.INTERNAL_SERVER_ERROR;
        }

        return RestResponse.status(responseStatus, error);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleKeycloakAdminTokenGenerationException(final KeycloakAdminTokenGenerationException kcAdminTokenGenException) {
        final Response errorResponse = kcAdminTokenGenException.getResponse();
        final int status = errorResponse.getStatus();

        final ErrorResponse error = new ErrorResponse();
        RestResponse.Status responseStatus;
        if (status == HttpResponseStatus.UNAUTHORIZED.code()) {
            error.title("Unauthorized.");
            error.message("You do not have permissions to generate a keycloak admin token!");
            error.status(HttpResponseStatus.UNAUTHORIZED.code());
            error.code(ErrorCodes.KC_ADMIN_TOKEN_CREATION_ERROR_CODE);
            responseStatus = RestResponse.Status.UNAUTHORIZED;
        } else {
            error.title("Internal server error.");
            error.message(kcAdminTokenGenException.getMessage());
            error.status(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
            error.code(ErrorCodes.INTERNAL_SERVER_ERROR_CODE);
            responseStatus = RestResponse.Status.INTERNAL_SERVER_ERROR;
        }

        return RestResponse.status(responseStatus, error);
    }
}
