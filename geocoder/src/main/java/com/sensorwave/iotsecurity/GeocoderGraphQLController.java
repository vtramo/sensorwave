package com.sensorwave.iotsecurity;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.openapi.quarkus.position_stack_api_yaml.model.ReverseGeocodingResult;

@GraphQLApi
public class GeocoderGraphQLController {

    @Inject
    GeocoderService geocoderService;

    @Query
    public Uni<ReverseGeocodingResult> reverseGeocoding(final Position position) {
        return geocoderService.reserveGeocoding(position);
    }
}
