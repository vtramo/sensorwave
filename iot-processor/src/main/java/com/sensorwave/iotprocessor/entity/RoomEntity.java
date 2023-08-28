package com.sensorwave.iotprocessor.entity;

import com.mongodb.MongoWriteException;
import com.sensorwave.iotprocessor.service.exceptions.RoomEntityCreationException;
import io.quarkus.logging.Log;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.NotFoundException;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;

@Setter
@Getter
@MongoEntity(collection = "rooms")
public class RoomEntity extends PanacheMongoEntity {

    private String name;
    private String ownerUsername;
    private List<RoomSmartObjectEntity> smartObjects = new ArrayList<>();
    private Instant createdAt = Instant.now();

    @Data
    public static class RoomSmartObjectEntity {
        public enum Status { ONLINE, OFFLINE }
        @BsonId private String id;
        private String name;
        private Status status = Status.OFFLINE;
        private Instant createdAt = Instant.now();
    }

    public static RoomEntity createRoom(final String roomOwnerUsername, final String roomName) {
        final RoomEntity roomEntity = new RoomEntity();
        roomEntity.setName(roomName);
        roomEntity.setOwnerUsername(roomOwnerUsername);

        try {
            persist(roomEntity);
        } catch (MongoWriteException e) {
            throw new RoomEntityCreationException(e.getError(), roomOwnerUsername, roomName);
        }

        return roomEntity;
    }

    public static List<RoomEntity> findRoomsByOwnerUsername(final String roomOwnerUsername) {
        final Document queryDocument = new Document();
        queryDocument.put("ownerUsername", roomOwnerUsername);
        return find(queryDocument).list();
    }

    public static RoomSmartObjectEntity createRoomSmartObject(
        final String roomName,
        final String roomOwnerUsername,
        final String smartObjectName
    ) {
        if (!existRoomByOwnerAndName(roomOwnerUsername, roomName)) throw new NotFoundException();

        final String smartObjectId = UUID.randomUUID().toString();
        final String updateDocJson = format("{ $push: { smartObjects: { _id: \"%s\", name: \"%s\" } } }", smartObjectId, smartObjectName);
        final Document updateDoc = Document.parse(updateDocJson);

        final String queryDocJson = format("""
            {
                "name": "%s",
                "ownerUsername": "%s",
                "smartObjects": { $not: { $elemMatch: { "name": "%s" } } }
            }
        """, roomName, roomOwnerUsername, smartObjectName);
        final Document queryDoc = Document.parse(queryDocJson);

        final long numUpdatedDocs = update(updateDoc).where(queryDoc);
        if (numUpdatedDocs != 1) throw new NotAllowedException("The name of the room already exists!");

        final RoomSmartObjectEntity roomSmartObjectEntity = findRoomSmartObjectById(roomName, roomOwnerUsername, smartObjectId);
        Log.info(roomSmartObjectEntity.toString());
        return roomSmartObjectEntity;
    }

    public static RoomSmartObjectEntity findRoomSmartObjectById(
        final String roomName,
        final String roomOwnerUsername,
        final String smartObjectId
    ) {
        final String queryDocJson = format("""
            { name: "%s", ownerUsername: "%s" },
            { smartObjects: { $elemMatch: { _id: "%s" } } }
        """, roomName, roomOwnerUsername, smartObjectId);
        final RoomEntity roomEntity = find(Document.parse(queryDocJson)).singleResult();
        return roomEntity.getSmartObjects().get(0);
    }

    public static boolean existRoomByOwnerAndName(final String roomOwnerUsername, final String roomName) {
        return findRoomByOwnerAndName(roomOwnerUsername, roomName).isPresent();
    }

    public static Optional<RoomEntity> findRoomByOwnerAndName(final String roomOwnerUsername, final String roomName) {
        final Document queryDoc = Document.parse(format("""
            { ownerUsername: "%s", name: "%s" }
        """, roomOwnerUsername, roomName));
        return find(queryDoc).singleResultOptional();
    }
}