package org.github.rmiot.config;

import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

public class MQTTClientConfig {

    @ConfigProperty(name = "mqtt.client.id", defaultValue = "iot-security")
    String id;

    @ConfigProperty(name = "mqtt.client.username", defaultValue = "iot-security")
    String username;

    @ConfigProperty(name = "mqtt.client.password", defaultValue = "password")
    String password;

    @ConfigProperty(name = "mqtt.client.maxInflightQueue", defaultValue = "500")
    int maxInflightQueue;

    @ConfigProperty(name = "mqtt.client.cleanSession", defaultValue = "false")
    boolean cleanSession;

    @Produces
    @ApplicationScoped
    MqttClient mqttClient() {
        MqttClientOptions mqttClientOptions = new MqttClientOptions();
        //mqttClientOptions.setClientId(id);
        mqttClientOptions.setCleanSession(cleanSession);
        mqttClientOptions.setMaxInflightQueue(maxInflightQueue);
        mqttClientOptions.setUsername(username);
        mqttClientOptions.setPassword(password);
        return MqttClient.create(Vertx.vertx(), mqttClientOptions);
    }
}
