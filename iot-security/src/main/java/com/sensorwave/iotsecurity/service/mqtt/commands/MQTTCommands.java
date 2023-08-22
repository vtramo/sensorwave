package com.sensorwave.iotsecurity.service.mqtt.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.buffer.Buffer;

import java.util.List;

public class MQTTCommands {

    @JsonProperty("commands") List<MQTTCommand> commands;

    public MQTTCommands(final List<MQTTCommand> commands) {
        this.commands = commands;
    }

    public Buffer toJsonBuffer() {
        return MQTTCommandSerializer.writeMqttCommandsToJsonBuffer(this);
    }
}
