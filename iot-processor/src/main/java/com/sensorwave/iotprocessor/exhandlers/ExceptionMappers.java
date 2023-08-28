package com.sensorwave.iotprocessor.exhandlers;

import com.mongodb.ErrorCategory;
import com.mongodb.WriteError;
import com.sensorwave.iotprocessor.service.exceptions.RoomEntityCreationException;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.openapi.quarkus.iot_processor_api_yaml.model.ErrorResponse;

import java.util.Objects;

public class ExceptionMappers {

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleRoomEntityCreationException(final RoomEntityCreationException roomEntityCreationException) {
        final WriteError writeError = roomEntityCreationException.getWriteError();
        final ErrorCategory errorCategory = writeError.getCategory();
        final String roomOwnerUsername = roomEntityCreationException.getRoomOwnerUsername();
        final String roomName = roomEntityCreationException.getRoomName();

        final ErrorResponse error = new ErrorResponse();
        RestResponse.Status responseStatus;
        if (Objects.equals(errorCategory.name(), ErrorCategory.DUPLICATE_KEY.name())) {
            error.title("Existing room name.");
            error.status(HttpResponseStatus.CONFLICT.code());
            error.message(String.format("User %s already has a room named %s!", roomOwnerUsername, roomName));
            error.code(ErrorCodes.ROOM_CREATION_DUP_NAME_ERROR_CODE);
            responseStatus = RestResponse.Status.CONFLICT;
        } else {
            error.title("Internal server error: cannot create the room.");
            error.status(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
            error.message(writeError.getMessage());
            error.code(ErrorCodes.INTERNAL_SERVER_ERROR_CODE);
            responseStatus = RestResponse.Status.INTERNAL_SERVER_ERROR;
        }

        return RestResponse.status(responseStatus, error);
    }
}
