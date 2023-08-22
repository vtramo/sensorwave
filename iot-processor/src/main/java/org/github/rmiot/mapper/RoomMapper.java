package org.github.rmiot.mapper;

import org.github.rmiot.entity.RoomEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.openapi.quarkus.iot_processor_api_yaml.model.Room;

@Mapper(componentModel = "cdi")
public interface RoomMapper {

    @AfterMapping
    default void convertObjectIdToString(RoomEntity roomEntity, @MappingTarget Room roomApi) {
        roomApi.id(roomEntity.id.toString());
    }

    @Mapping(ignore = true, target = "id")
    @Mapping(source = "ownerUsername", target = "roomOwnerUsername")
    Room toRoomApi(RoomEntity roomEntity);
}
