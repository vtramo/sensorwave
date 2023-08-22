package org.github.rmiot.service.mqtt.commands;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MQTTRole {

    @JsonProperty("rolename") String rolename;
    int priority;
}
