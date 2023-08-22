package com.sensorwave.iotsecurity.service.mqtt.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.smallrye.common.constraint.NotNull;
import io.vertx.core.buffer.Buffer;

public abstract class MQTTCommand {

    static final String CREATE_CLIENT_COMMAND_NAME = "createClient";
    static final String GET_CLIENT_COMMAND_NAME = "getClient";

    @NotNull
    @JsonProperty("command")
    final String command;

    MQTTCommand(String command) {
        this.command = command;
    }

    Buffer toJsonBuffer() {
        return MQTTCommandSerializer.writeMqttCommandToJsonBuffer(this);
    }
}
