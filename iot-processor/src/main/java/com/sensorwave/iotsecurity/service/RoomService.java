package com.sensorwave.iotsecurity.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.SecurityContext;
import com.sensorwave.iotsecurity.entity.RoomEntity;
import com.sensorwave.iotsecurity.mapper.RoomMapper;
import org.openapi.quarkus.iot_processor_api_yaml.model.Room;

import java.security.Principal;
import java.util.List;
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
        final RoomEntity roomEntity = RoomEntity.createRoom(roomOwnerUsername);
        final String createdRoomId = String.valueOf(roomEntity.id);
        subscribeToRoom(createdRoomId);

        return roomMapper.toRoomApi(roomEntity);
    }

    private void subscribeToRoom(String createdRoomId) {
        final Future<Boolean> successfullySubscribedFuture = mqttRoomService.subscribeToRoom(createdRoomId);
        boolean successfullySubscribed = false;
        try {
            successfullySubscribed = successfullySubscribedFuture.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        } finally {
            if (!successfullySubscribed) {
                RoomEntity.deleteById(createdRoomId);
            }
        }
    }

    public List<Room> getRoomsByOwnerUsername(String roomOwnerUsername) {
        final List<RoomEntity> roomEntities = RoomEntity.findRoomsByOwnerUsername(roomOwnerUsername);
        return roomEntities.stream()
            .map(roomMapper::toRoomApi)
            .toList();
    }
}
