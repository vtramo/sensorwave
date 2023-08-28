package com.sensorwave.iotprocessor.mapper;

import com.sensorwave.iotprocessor.entity.RoomEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.openapi.quarkus.iot_processor_api_yaml.model.Room;
import org.openapi.quarkus.iot_processor_api_yaml.model.RoomSmartObject;

import static com.sensorwave.iotprocessor.entity.RoomEntity.*;

@Mapper(componentModel = "cdi")
public interface RoomMapper {

    @AfterMapping
    default void convertObjectIdToString(RoomEntity roomEntity, @MappingTarget Room roomApi) {
        roomApi.id(roomEntity.id.toHexString());
    }

    @Mapping(ignore = true, target = "id")
    @Mapping(source = "ownerUsername", target = "roomOwnerUsername")
    Room toRoomApi(RoomEntity roomEntity);


    RoomSmartObject toRoomSmartObjectApi(RoomSmartObjectEntity roomSmartObjectEntity);
}
