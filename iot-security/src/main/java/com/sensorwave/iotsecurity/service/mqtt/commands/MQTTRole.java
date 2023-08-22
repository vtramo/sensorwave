package com.sensorwave.iotsecurity.service.mqtt.commands;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MQTTRole {

    @JsonProperty("rolename") String rolename;
    int priority;
}
