package org.github.rmiot.service.mqtt.commands.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.smallrye.common.constraint.NotNull;
import org.github.rmiot.service.mqtt.commands.MQTTGroup;
import org.github.rmiot.service.mqtt.commands.MQTTRole;

import java.util.List;

public class MQTTGetClientResponse implements MQTTCommandResponse {

    @NotNull
    @JsonProperty("command")
    public String command;

    @NotNull
    @JsonProperty("data")
    public Data data;

    public static class Data {
        @NotNull
        @JsonProperty("client")
        public Client client;
    }

    public static class Client {
        @NotNull @JsonProperty("username") public String username;
        @NotNull @JsonProperty("clientid") public String clientId;
        @NotNull @JsonProperty("textname") public String textName;
        @NotNull @JsonProperty("textdescription") public String textDescription;
        @NotNull @JsonProperty("roles") public List<MQTTRole> roles;
        @NotNull @JsonProperty("groups") public List<MQTTGroup> groups;
    }

    public String getCommand() {
        return command;
    }

    public String getUsername() {
        return data.client.username;
    }
}
