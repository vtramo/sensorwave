package org.github.rmiot.service;

import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.messages.MqttConnAckMessage;
import io.vertx.mqtt.messages.MqttPublishMessage;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.github.rmiot.config.MQTTBrokerConfig;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class MQTTAbstractService {

    @Inject
    MQTTBrokerConfig mqttBrokerConfig;

    @Inject
    MqttClient mqttClient;

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

    public final boolean isConnected() {
        return mqttClient.isConnected();
    }

    void handleMqttConnAckMessage(final MqttConnAckMessage mqttConnAckMessage) {
        Log.infof("Successfully connected to the MQTT broker %s:%d. Session is present flag: %b. MQTT Properties: %s",
            mqttBrokerConfig.host(),
            mqttBrokerConfig.port(),
            mqttConnAckMessage.isSessionPresent(),
            mqttConnAckMessage.properties().listAll()
        );

        mqttClient.publishHandler(this::handleMqttPublishMessage);
    }

    void handleConnectionToMqttBrokerFailed(final Throwable throwable) {
        Log.errorf(throwable, "Error connecting to MQTT broker %s:%d.",
            mqttBrokerConfig.host(),
            mqttBrokerConfig.port()
        );

        Log.info("I will try again to reconnect to the MQTT broker in 5 seconds...");
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(this::connectToMqttBroker, 5, TimeUnit.SECONDS);
    }

    void handleMqttPublishMessage(final MqttPublishMessage mqttPublishMessage) {
        final String topicName = mqttPublishMessage.topicName();
        Log.infof("New message from topicName %s. QoS %s. Payload: %s. Properties: %s.",
            topicName,
            mqttPublishMessage.qosLevel(),
            mqttPublishMessage.payload(),
            mqttPublishMessage.properties().listAll()
        );
    }
}
