package org.github.rmiot.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "mqtt.broker")
public interface MQTTBrokerConfig {

    @WithDefault("localhost")
    String host();

    @WithDefault("32777")
    int port();
}
