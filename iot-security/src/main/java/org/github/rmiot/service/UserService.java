package org.github.rmiot.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.github.rmiot.config.KeycloakConfig;
import org.github.rmiot.service.mqtt.MQTTSecurityService;
import org.openapi.quarkus.iot_security_api_yaml.model.User;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ApplicationScoped
public class UserService {

    @Inject MQTTSecurityService mqttSecurityService;
    @Inject KeycloakService keycloakUserService;
    @Inject KeycloakConfig keycloakConfig;

    public User registerUser(final String username, final String password) {
        final String createdUserId = keycloakUserService.createUser(keycloakConfig.realm(), username, password);
        createMqttUser(username, password, createdUserId);

        return new User()
            .username(username);
    }

    private void createMqttUser(String username, String password, String createdUserId) {
        final Future<Boolean> isRegisteredCorrectlyFuture = mqttSecurityService.createUser(username, password);
        boolean isRegisteredCorrectly = false;
        try {
            isRegisteredCorrectly = isRegisteredCorrectlyFuture.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        } finally {
            if (!isRegisteredCorrectly) {
                keycloakUserService.deleteUser(keycloakConfig.realm(), createdUserId);
            }
        }
    }
}
