package com.sensorwave.iotprocessor.service.exceptions;

public class InvalidTopicNameException extends RuntimeException {

    public InvalidTopicNameException(final String message) {
        super(message);
    }
}
