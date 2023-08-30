package com.sensorwave.iotprocessor.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Setter
@Getter
@MongoEntity(collection = "smartobject-messages")
public class SmartObjectMessageEntity extends PanacheMongoEntity {

    private Instant timestamp;
    private SmartObjectMessageMetadata metadata;
    private List<Data> data;

    @Getter
    @Setter
    public static class SmartObjectMessageMetadata {
        private String roomId;
        private String smartObjectId;
    }

    public sealed interface Data permits TemperatureData, PositionData, HumidityData, StatusData { }

    public record TemperatureData(double temperature) implements Data { }
    public record PositionData(double latitude, double longitude) implements Data { }
    public record HumidityData(double temperature) implements Data { }
    public record StatusData(boolean isOnline) implements Data { }
}
