package com.sensorwave.iotsecurity.service.mqtt.commands;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MQTTGroup {
    @JsonProperty("groupname") String groupName;
    int priority;
}
