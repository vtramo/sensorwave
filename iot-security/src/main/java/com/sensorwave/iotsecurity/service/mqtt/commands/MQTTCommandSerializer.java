package com.sensorwave.iotsecurity.service.mqtt.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.buffer.Buffer;

public abstract class MQTTCommandSerializer {

    public static String convertMqttCommandToJsonString(final MQTTCommand mqttCommand) {
        final ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.writeValueAsString(mqttCommand);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String convertMqttCommandsToJsonString(final MQTTCommands mqttCommands) {
        final ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.writeValueAsString(mqttCommands);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public static Buffer writeMqttCommandToJsonBuffer(final MQTTCommand mqttCommand) {
        final String mqttCommandJsonString = convertMqttCommandToJsonString(mqttCommand);
        return Buffer.buffer(mqttCommandJsonString);
    }

    public static Buffer writeMqttCommandsToJsonBuffer(final MQTTCommands mqttCommands) {
        final String mqttCommandsJsonString = convertMqttCommandsToJsonString(mqttCommands);
        return Buffer.buffer(mqttCommandsJsonString);
    }
}
