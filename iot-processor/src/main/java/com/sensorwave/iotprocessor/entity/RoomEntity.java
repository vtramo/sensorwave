package com.sensorwave.iotprocessor.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@MongoEntity(collection = "rooms")
public class RoomEntity extends PanacheMongoEntity {

    private String ownerUsername;
    private List<RoomSmartObject> smartObjects = new ArrayList<>();
    private Instant createdAt = Instant.now();

    public static class RoomSmartObject {
        private enum Status { ONLINE, OFFLINE }
        private String id;
        private Status status;
        private Instant joinedOn = Instant.now();
        private boolean exited;
        private Instant exitedOn;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public Instant getJoinedOn() {
            return joinedOn;
        }

        public void setJoinedOn(Instant joinedOn) {
            this.joinedOn = joinedOn;
        }

        public boolean isExited() {
            return exited;
        }

        public void setExited(boolean exited) {
            this.exited = exited;
        }

        public Instant getExitedOn() {
            return exitedOn;
        }

        public void setExitedOn(Instant exitedOn) {
            this.exitedOn = exitedOn;
        }
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public List<RoomSmartObject> getSmartObjects() {
        return smartObjects;
    }

    public void setSmartObjects(List<RoomSmartObject> smartObjects) {
        this.smartObjects = smartObjects;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public static RoomEntity createRoom(final String roomOwnerUsername) {
        final RoomEntity roomEntity = new RoomEntity();
        roomEntity.setOwnerUsername(roomOwnerUsername);
        persist(roomEntity);
        return roomEntity;
    }

    public static List<RoomEntity> findRoomsByOwnerUsername(final String roomOwnerUsername) {
        final Document queryDocument = new Document();
        queryDocument.put("ownerUsername", roomOwnerUsername);
        return find(queryDocument).list();
    }
}