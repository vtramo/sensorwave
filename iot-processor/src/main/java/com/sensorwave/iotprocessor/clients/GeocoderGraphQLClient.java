package com.sensorwave.iotprocessor.clients;

import com.sensorwave.iotprocessor.config.GeocoderServiceClientConfig;
import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.graphql.Query;

@GraphQLClientApi(configKey = GeocoderServiceClientConfig.NAME)
public interface GeocoderGraphQLClient {

    @Query("reverseGeocoding")
    Uni<ReverseGeocodingResult> reverseGeocoding(Position position);
}
