package com.sensorwave.iotsecurity.service.mqtt;

import com.sensorwave.iotsecurity.config.MQTTBrokerConfig;
import com.sensorwave.iotsecurity.service.mqtt.commands.*;
import com.sensorwave.iotsecurity.service.mqtt.commands.responses.MQTTCommandResponse;
import com.sensorwave.iotsecurity.service.mqtt.commands.responses.MQTTCommandResponses;
import com.sensorwave.iotsecurity.service.mqtt.commands.responses.MQTTCreateClientResponse;
import com.sensorwave.iotsecurity.service.mqtt.commands.responses.MQTTGetClientResponse;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.messages.MqttConnAckMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class MQTTSecurityService {

    private static final String MQTT_CONTROL_TOPIC_API = "$CONTROL/dynamic-security/v1";

    @Inject
    MQTTBrokerConfig mqttBrokerConfig;

    @Inject
    MqttClient mqttClient;

    final Map<String, CompletableFuture<MQTTGetClientResponse>> getClientFuturesByUsername = new HashMap<>();


    void init(@Observes StartupEvent startupEvent) {
        connectToMqttBroker();
    }

    private void connectToMqttBroker() {
        final String mqttBrokerHost = mqttBrokerConfig.host();
        final int mqttBrokerPort = mqttBrokerConfig.port();

        mqttClient.connect(mqttBrokerPort, mqttBrokerHost)
            .onSuccess(this::handleMqttConnAckMessage)
            .onFailure(this::handleConnectionToMqttBrokerFailed);
    }

    private void handleMqttConnAckMessage(final MqttConnAckMessage mqttConnAckMessage) {
        Log.infof("Successfully connected to the MQTT broker %s:%d. Session is present flag: %b. MQTT Properties: %s",
            mqttBrokerConfig.host(),
            mqttBrokerConfig.port(),
            mqttConnAckMessage.isSessionPresent(),
            mqttConnAckMessage.properties().listAll()
        );

        mqttClient.subscribe(MQTT_CONTROL_TOPIC_API + "/response", MqttQoS.EXACTLY_ONCE.value());
        mqttClient.publishHandler(mqttPublishMessage -> {
            final Buffer payload = mqttPublishMessage.payload();

            final MQTTCommandResponses mqttCommandResponses = MQTTCommandResponseDeserializer.decodeMqttCommandResponses(payload);
            for (MQTTCommandResponse mqttCommandResponse: mqttCommandResponses.responses) {
                if (mqttCommandResponse instanceof MQTTCreateClientResponse) continue;
                if (mqttCommandResponse instanceof MQTTGetClientResponse) {
                    completeGetClientFutureResponse((MQTTGetClientResponse) mqttCommandResponse);
                }
            }
        });
    }

    private void handleConnectionToMqttBrokerFailed(final Throwable throwable) {
        Log.errorf(throwable, "Error connecting to MQTT broker %s:%d.",
            mqttBrokerConfig.host(),
            mqttBrokerConfig.port()
        );

        Log.info("I will try again to reconnect to the MQTT broker in 5 seconds...");
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(this::connectToMqttBroker, 5, TimeUnit.SECONDS);
    }

    public void completeGetClientFutureResponse(final MQTTGetClientResponse mqttGetClientResponse) {
        final String username = mqttGetClientResponse.getUsername();
        final CompletableFuture<MQTTGetClientResponse> getClientFuture = getClientFuturesByUsername.remove(username);
        getClientFuture.complete(mqttGetClientResponse);
    }

    public CompletableFuture<Boolean> createUser(final String username, final String password) {
        final MQTTCommand createUserMqttCommand = new MQTTCreateClientCommand()
            .username(username)
            .password(password)
            .clientId(username);
        final Buffer mqttCommands = new MQTTCommands(List.of(createUserMqttCommand)).toJsonBuffer();

        final CompletableFuture<Boolean> isRegisteredCorrectlyFuture = new CompletableFuture<>();

        mqttClient.publish(
            MQTT_CONTROL_TOPIC_API,
            mqttCommands,
            MqttQoS.EXACTLY_ONCE,
            false,
            false,
            asyncPublishPacketId -> completeRegistrationOnSuccess(username, isRegisteredCorrectlyFuture)
        );

        return isRegisteredCorrectlyFuture;
    }

    private void completeRegistrationOnSuccess(final String username, final CompletableFuture<Boolean> isRegisteredCorrectlyFuture) {
        final CompletableFuture<MQTTGetClientResponse> getClientFuture = getClient(username);
        getClientFuture.thenAccept(getClientResponse -> {
            final String registeredUsername = getClientResponse.getUsername();
            isRegisteredCorrectlyFuture.complete(Objects.equals(username, registeredUsername));
        });
    }

    public CompletableFuture<MQTTGetClientResponse> getClient(final String username) {
        final MQTTCommand getClientMqttCommand = new MQTTGetClientCommand().username(username);
        final Buffer mqttCommands = new MQTTCommands(List.of(getClientMqttCommand)).toJsonBuffer();

        final CompletableFuture<MQTTGetClientResponse> getClientFuture = new CompletableFuture<>();
        getClientFuturesByUsername.put(username, getClientFuture);

        mqttClient.publish(
            MQTT_CONTROL_TOPIC_API,
            mqttCommands,
            MqttQoS.EXACTLY_ONCE,
            false,
            false,
            asyncPackedId -> {}
        );

        return getClientFuture;
    }
}
