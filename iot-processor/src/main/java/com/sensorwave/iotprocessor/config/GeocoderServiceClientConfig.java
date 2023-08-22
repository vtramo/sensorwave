package com.sensorwave.iotprocessor.config;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@StaticInitSafe
@ConfigMapping(prefix = "geocoder.service.client")
public interface GeocoderServiceClientConfig {

    String NAME = "geocoder";

    @WithDefault("localhost")
    String host();

    @WithDefault("8091")
    String port();
}
