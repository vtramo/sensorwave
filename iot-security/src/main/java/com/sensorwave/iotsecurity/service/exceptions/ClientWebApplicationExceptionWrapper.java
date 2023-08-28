package com.sensorwave.iotsecurity.service.exceptions;

import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

public abstract class ClientWebApplicationExceptionWrapper extends ClientWebApplicationException {

    private final ClientWebApplicationException clientWebAppException;

    public ClientWebApplicationExceptionWrapper(ClientWebApplicationException clientWebAppException) {
        this.clientWebAppException = clientWebAppException;
    }

    @Override
    public Response getResponse() {
        return clientWebAppException.getResponse();
    }

    @Override
    public Throwable getCause() {
        return clientWebAppException.getCause();
    }

    @Override
    public String getMessage() {
        return clientWebAppException.getMessage();
    }

    @Override
    public String getLocalizedMessage() {
        return clientWebAppException.getLocalizedMessage();
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return clientWebAppException.getStackTrace();
    }
}
