package com.sensorwave.iotsecurity.service.mqtt.commands;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.smallrye.common.constraint.NotNull;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

public class MQTTCreateClientCommand extends MQTTCommand {

    @NotNull
    @JsonProperty("username")
    String username;

    @NotNull
    @JsonProperty("password")
    String password;

    @JsonInclude(Include.NON_NULL)
    @JsonProperty("clientid")
    String clientId;

    @JsonInclude(Include.NON_NULL)
    @JsonProperty("textname")
    String textName;

    @JsonInclude(Include.NON_NULL)
    @JsonProperty("textdescription")
    String textDescription;

    @JsonInclude(Include.NON_NULL)
    @JsonProperty("roles")
    List<MQTTRole> roles;

    @JsonInclude(Include.NON_NULL)
    @JsonProperty("groups")
    List<MQTTGroup> groups;

    public MQTTCreateClientCommand() {
        super(MQTTCommand.CREATE_CLIENT_COMMAND_NAME);
    }

    public MQTTCreateClientCommand username(final String username) {
        this.username = username;
        return this;
    }

    public MQTTCreateClientCommand password(final String password) {
        this.password = password;
        return this;
    }

    public MQTTCreateClientCommand clientId(final String clientId) {
        this.clientId = clientId;
        return this;
    }

    public MQTTCreateClientCommand textName(final String textName) {
        this.textName = textName;
        return this;
    }

    public MQTTCreateClientCommand textDescription(final String textDescription) {
        this.textDescription = textDescription;
        return this;
    }

    MQTTCreateClientCommand roles(final List<MQTTRole> roles) {
        this.roles = roles;
        return this;
    }

    MQTTCreateClientCommand groups(final List<MQTTGroup> groups) {
        this.groups = groups;
        return this;
    }
}
