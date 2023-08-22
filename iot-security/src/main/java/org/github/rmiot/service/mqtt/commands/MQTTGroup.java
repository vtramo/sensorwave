package org.github.rmiot.service.mqtt.commands;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MQTTGroup {
    @JsonProperty("groupname") String groupName;
    int priority;
}
