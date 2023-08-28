package com.sensorwave.iotprocessor.service.exceptions;

import com.mongodb.WriteError;
import lombok.Getter;

@Getter
public class RoomEntityCreationException extends RuntimeException {

    private final WriteError writeError;
    private final String roomOwnerUsername;
    private final String roomName;

    public RoomEntityCreationException(
        final WriteError writeError,
        final String roomOwnerUsername,
        final String roomName
    ) {
        this.writeError = writeError;
        this.roomOwnerUsername = roomOwnerUsername;
        this.roomName = roomName;
    }
}
