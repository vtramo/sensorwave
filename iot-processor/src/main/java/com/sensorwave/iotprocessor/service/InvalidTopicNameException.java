package com.sensorwave.iotprocessor.service;

public class InvalidTopicNameException extends RuntimeException {

    public InvalidTopicNameException(final String message) {
        super(message);
    }
}
