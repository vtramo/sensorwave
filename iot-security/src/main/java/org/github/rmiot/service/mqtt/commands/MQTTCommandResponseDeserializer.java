package org.github.rmiot.service.mqtt.commands;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.vertx.core.buffer.Buffer;
import org.github.rmiot.service.mqtt.commands.responses.MQTTCommandResponse;
import org.github.rmiot.service.mqtt.commands.responses.MQTTCommandResponses;
import org.github.rmiot.service.mqtt.commands.responses.MQTTCreateClientResponse;
import org.github.rmiot.service.mqtt.commands.responses.MQTTGetClientResponse;

import java.io.IOException;
import java.util.Objects;

public class MQTTCommandResponseDeserializer extends StdDeserializer<MQTTCommandResponse> {
    MQTTCommandResponseDeserializer() {
        this((Class<?>) null);
    }

    protected MQTTCommandResponseDeserializer(Class<?> vc) {
        super(vc);
    }

    protected MQTTCommandResponseDeserializer(JavaType valueType) {
        super(valueType);
    }

    protected MQTTCommandResponseDeserializer(StdDeserializer<?> src) {
        super(src);
    }

    @Override
    public MQTTCommandResponse deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        final ObjectCodec objectCodec = jsonParser.getCodec();
        final JsonNode jsonNode = objectCodec.readTree(jsonParser);
        final String json = jsonNode.toString();
        final String command = jsonNode.get("command").asText();

        final ObjectMapper objectMapper = new ObjectMapper();
        if (Objects.equals(command, MQTTCommand.CREATE_CLIENT_COMMAND_NAME)) {
            return objectMapper.readValue(json, MQTTCreateClientResponse.class);
        } else if (Objects.equals(command, MQTTCommand.GET_CLIENT_COMMAND_NAME)) {
            return objectMapper.readValue(json, MQTTGetClientResponse.class);
        } else {
            throw new RuntimeException("Unknown MQTT Command Response!");
        }
    }

    public static MQTTCommandResponses decodeMqttCommandResponses(final Buffer buffer) {
        final byte[] bytes = buffer.getBytes();
        final ObjectMapper mapper = new ObjectMapper();
        final SimpleModule module = new SimpleModule();
        module.addDeserializer(MQTTCommandResponse.class, new MQTTCommandResponseDeserializer());
        mapper.registerModule(module);
        try {
            return mapper.readValue(bytes, MQTTCommandResponses.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
