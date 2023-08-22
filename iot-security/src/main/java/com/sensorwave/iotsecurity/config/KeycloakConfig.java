package com.sensorwave.iotsecurity.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "keycloak.app")
public interface KeycloakConfig {
    String DEFAULT_GRANT_TYPE = "client_credentials";
    String REALM_MASTER_NAME = "master";
    String CREDENTIAL_REPRESENTATION_TYPE_PASSWORD = "password";

    @WithDefault("localhost")
    String host();

    @WithDefault("32778")
    String port();

    @WithDefault("quarkus")
    String realm();

    @WithName("admin-cli")
    AdminCli adminCliConfig();

    interface AdminCli {
        String id();
        String secret();
    }
}
