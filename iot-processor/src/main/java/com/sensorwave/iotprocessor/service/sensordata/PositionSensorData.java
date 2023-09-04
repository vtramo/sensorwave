package com.sensorwave.iotprocessor.service.sensordata;

import java.util.ArrayList;
import java.util.List;

public record PositionSensorData(
    SensorDataType sensorDataType,
    GeoJsonPoint position
) implements SensorData {

    public record GeoJsonPoint(String type, List<Double> coordinates) {
        private static final String geoJsonTypePoint = "Point";

        GeoJsonPoint(List<Double> coordinates) {
            this(geoJsonTypePoint, coordinates);
        }

        public static GeoJsonPoint toGeoJsonPoint(final double longitude, final double latitude) {
            return new GeoJsonPoint(new ArrayList<>(2) { { add(longitude); add(latitude); } });
        }
    }

    PositionSensorData(final double longitude, final double latitude) {
        this(SensorDataType.POSITION, GeoJsonPoint.toGeoJsonPoint(longitude, latitude));
    }

    @Override
    public SensorDataType getSensorDataType() {
        return sensorDataType;
    }
}