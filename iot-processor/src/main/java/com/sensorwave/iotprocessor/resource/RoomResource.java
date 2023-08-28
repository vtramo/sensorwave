package com.sensorwave.iotprocessor.resource;

import com.sensorwave.iotprocessor.service.RoomService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.openapi.quarkus.iot_processor_api_yaml.model.Room;
import org.openapi.quarkus.iot_processor_api_yaml.model.RoomSmartObject;

import java.util.List;

@Path("rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {
    @Inject RoomService roomService;

    @POST
    public Room createRoom(final Room room) {
        return roomService.createRoom(room);
    }

    @GET
    @Path("{roomOwnerUsername}")
    public List<Room> getRoomsByOwnerUsername(@PathParam(value = "roomOwnerUsername") final String roomOwnerUsername) {
        return roomService.getRoomsByOwnerUsername(roomOwnerUsername);
    }

    @POST
    @Path("{roomName}/smartobjects")
    public RoomSmartObject createRoomSmartObject(@PathParam(value = "roomName") final String roomName, final RoomSmartObject roomSmartObject) {
        return roomService.createRoomSmartObject(roomName, roomSmartObject);
    }
}
