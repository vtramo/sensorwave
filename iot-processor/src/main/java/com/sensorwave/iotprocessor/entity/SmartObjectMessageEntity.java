package com.sensorwave.iotprocessor.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@MongoEntity(collection = "smartobject-messages")
public class SmartObjectMessageEntity extends PanacheMongoEntity {

    private Instant timestamp;
    private SmartObjectMessageMetadata metadata;

    @Getter
    @Setter
    public static class SmartObjectMessageMetadata {
        private String roomId;
        private String smartObjectId;
    }
}
