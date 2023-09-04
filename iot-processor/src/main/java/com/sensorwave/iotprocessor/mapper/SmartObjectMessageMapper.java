package com.sensorwave.iotprocessor.mapper;

import com.google.protobuf.Timestamp;
import com.sensorwave.iotprocessor.SmartObjectMessage;
import com.sensorwave.iotprocessor.entity.SmartObjectMessageEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.Instant;

import static com.sensorwave.iotprocessor.entity.SmartObjectMessageEntity.SmartObjectMessageMetadata;

@Mapper(componentModel = "jakarta", uses = {SensorDataMapper.class})
public interface SmartObjectMessageMapper {

    @AfterMapping
    default void setSmartObjectMessageEntityMetadata(
        SmartObjectMessage smartObjectMessage,
        @MappingTarget SmartObjectMessageEntity smartObjectMessageEntity
    ) {
        final SmartObjectMessageMetadata smartObjectMessageEntityMetadata = new SmartObjectMessageMetadata();
        smartObjectMessageEntityMetadata.setSmartObjectId(smartObjectMessage.getSmartObjectId());
        smartObjectMessageEntityMetadata.setRoomId(smartObjectMessage.getRoomId());
        smartObjectMessageEntity.setMetadata(smartObjectMessageEntityMetadata);
    }

    @AfterMapping
    default void setSmartObjectMessageEntityTimestamp(
        SmartObjectMessage smartObjectMessage,
        @MappingTarget SmartObjectMessageEntity smartObjectMessageEntity
    ) {
        final Timestamp timestamp = smartObjectMessage.getTimestamp();
        smartObjectMessageEntity.setTimestamp(Instant.ofEpochSecond(timestamp.getSeconds()));
    }

    @Mapping(target = "timestamp", ignore = true)
    @Mapping(target = "data", source = "dataList")
    SmartObjectMessageEntity toSmartObjectMessageEntity(SmartObjectMessage smartObjectMessage);
}
