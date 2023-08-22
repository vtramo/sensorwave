package com.sensorwave.iotsecurity.service.mqtt.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.smallrye.common.constraint.NotNull;

public class MQTTGetClientCommand extends MQTTCommand {

    public MQTTGetClientCommand() {
        super(MQTTCommand.GET_CLIENT_COMMAND_NAME);
    }

    @NotNull
    @JsonProperty
    String username;

    public MQTTGetClientCommand username(final String username) {
        this.username = username;
        return this;
    }
}
