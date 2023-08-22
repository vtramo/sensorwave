package com.sensorwave.geocoder;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.openapi.quarkus.position_stack_api_yaml.api.GeocodingApi;
import org.openapi.quarkus.position_stack_api_yaml.model.ReverseGeocodingResult;

@Singleton
public class GeocoderService {

    @ConfigProperty(name = "api.positionstack.access-key")
    String accessKeyPositionStack;

    @RestClient
    @Inject
    GeocodingApi geocodingApi;

    public Uni<ReverseGeocodingResult> reserveGeocoding(final Position position) {
        return geocodingApi.reverseGet(
            accessKeyPositionStack,
            String.valueOf(position),
            null,
            null,
            null,
            null
        );
    }

}
