package org.github.rmiot.service.mqtt.commands.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.smallrye.common.constraint.NotNull;

public class MQTTCreateClientResponse implements MQTTCommandResponse {

    @NotNull
    @JsonProperty("command")
    public String command;

    public String getCommand() {
        return command;
    }
}
