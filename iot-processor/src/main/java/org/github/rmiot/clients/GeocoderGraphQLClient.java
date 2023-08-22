package org.github.rmiot.clients;

import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.graphql.Query;
import org.github.rmiot.config.GeocoderServiceClientConfig;

@GraphQLClientApi(configKey = GeocoderServiceClientConfig.NAME)
public interface GeocoderGraphQLClient {

    @Query("reverseGeocoding")
    Uni<ReverseGeocodingResult> reverseGeocoding(Position position);
}
