package com.sensorwave.iotsecurity.service.mqtt.commands.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.smallrye.common.constraint.NotNull;

import java.util.List;

public class MQTTCommandResponses {

    @NotNull
    @JsonProperty("responses")
    public List<MQTTCommandResponse> responses;
}
