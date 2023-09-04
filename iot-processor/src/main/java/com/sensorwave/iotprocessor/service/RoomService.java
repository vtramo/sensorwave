package com.sensorwave.iotprocessor.service;

import com.sensorwave.iotprocessor.entity.RoomEntity;
import com.sensorwave.iotprocessor.entity.SmartObjectMessageEntity;
import com.sensorwave.iotprocessor.mapper.RoomMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.SecurityContext;
import org.openapi.quarkus.iot_processor_api_yaml.model.Room;
import org.openapi.quarkus.iot_processor_api_yaml.model.RoomSmartObject;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ApplicationScoped
public class RoomService {

    @Inject SecurityContext securityContext;
    @Inject MQTTRoomService mqttRoomService;
    @Inject RoomMapper roomMapper;

    public Room createRoom(final Room room) {
        if (!mqttRoomService.isConnected()) throw new RuntimeException("MQTT not connected!");

        final Principal userPrincipal = securityContext.getUserPrincipal();
        final String roomOwnerUsername = userPrincipal.getName();
        final String roomName = room.getName();
        final RoomEntity roomEntity = RoomEntity.createRoom(roomOwnerUsername, roomName);
        final String createdRoomId = String.valueOf(roomEntity.id);

        return roomMapper.toRoomApi(roomEntity);
    }

    public List<Room> getRoomsByOwnerUsername(String roomOwnerUsername) {
        assertUsernameMatchSecurityContext(roomOwnerUsername);

        final List<RoomEntity> roomEntities = RoomEntity.findRoomsByOwnerUsername(roomOwnerUsername);
        return roomEntities.stream()
            .map(roomMapper::toRoomApi)
            .toList();
    }

    public RoomSmartObject createRoomSmartObject(final String roomName, final RoomSmartObject roomSmartObject) {
        final String roomOwnerUsername = roomSmartObject.getRoomOwnerUsername();
        assertUsernameMatchSecurityContext(roomSmartObject.getRoomOwnerUsername());

        final RoomEntity.RoomSmartObjectEntity roomSmartObjectEntity = RoomEntity.createRoomSmartObject(
            roomName,
            roomOwnerUsername,
            roomSmartObject.getName()
        );

        final String roomId = RoomEntity
            .findRoomByOwnerAndName(roomOwnerUsername, roomName)
            .map(room -> room.id.toHexString())
            .orElseThrow(RuntimeException::new);

        mqttRoomService.subscribeToSmartObjectRoom(roomId, roomSmartObjectEntity.getId());
        return roomMapper
            .toRoomSmartObjectApi(roomSmartObjectEntity)
            .roomOwnerUsername(roomOwnerUsername);
    }

    private void assertUsernameMatchSecurityContext(final String roomOwnerUsername) {
        final Principal userPrincipal = securityContext.getUserPrincipal();
        if (!Objects.equals(userPrincipal.getName(), roomOwnerUsername)) {
            throw new ForbiddenException();
        }
    }

    private void subscribeToRoomSmartObject(final String roomId, final String smartObjectId) {
        final Future<Boolean> successfullySubscribedFuture = mqttRoomService.subscribeToSmartObjectRoom(roomId, smartObjectId);
        boolean successfullySubscribed = false;
        try {
            successfullySubscribed = successfullySubscribedFuture.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        } finally {
            if (!successfullySubscribed) {
                SmartObjectMessageEntity.deleteById(smartObjectId);
            }
        }
    }
}
