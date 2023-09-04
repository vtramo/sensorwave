package com.sensorwave.iotprocessor.entity;

import com.sensorwave.iotprocessor.service.sensordata.SensorData;
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
    private List<SensorData> data;

    @Getter
    @Setter
    public static class SmartObjectMessageMetadata {
        private String roomId;
        private String smartObjectId;
    }
}
