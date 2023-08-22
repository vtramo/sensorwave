package org.github.rmiot.service.mqtt.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import io.vertx.core.buffer.Buffer;
import org.github.rmiot.service.mqtt.commands.responses.MQTTCommandResponses;
import org.github.rmiot.service.mqtt.commands.responses.MQTTCreateClientResponse;
import org.github.rmiot.service.mqtt.commands.responses.MQTTGetClientResponse;

import java.io.IOException;

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
