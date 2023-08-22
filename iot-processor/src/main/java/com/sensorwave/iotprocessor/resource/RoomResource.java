package com.sensorwave.iotprocessor.resource;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.sensorwave.iotprocessor.model.Cat;
import com.sensorwave.iotprocessor.model.ErrorStatus;
import com.sensorwave.iotprocessor.model.Message;
import com.sensorwave.iotprocessor.service.RoomService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.openapi.quarkus.iot_processor_api_yaml.model.Room;

import java.util.List;

@Path("rooms")
@Produces(MediaType.TEXT_PLAIN)
public class RoomResource {
    @Inject
    RoomService roomService;

    @POST
    public Room createRoom(final Room room) {
        return roomService.createRoom(room);
    }

    @GET
    @Path("{roomOwnerUsername}")
    public List<Room> getRoomsByOwnerUsername(@PathParam(value = "roomOwnerUsername") final String roomOwnerUsername) {
        return roomService.getRoomsByOwnerUsername(roomOwnerUsername);
    }

    @GET
    public String a() throws InvalidProtocolBufferException {
        Cat cat = Cat.newBuilder().setType("cat").build();
        Message message = Message.newBuilder().setValue("message").build();
        Any anyCat = Any.pack(cat);
        Any anyMessage = Any.pack(message);
        ErrorStatus errorStatus = ErrorStatus.newBuilder().addDetails(anyCat).addDetails(anyMessage).build();
        ErrorStatus errorStatus1 = ErrorStatus.parseFrom(errorStatus.toByteArray());
        return "ciao " + errorStatus1.getDetails(0).is(Cat.class) + " " + errorStatus1.getDetails(1).is(Message.class)
                + " " + errorStatus1.getDetails(0).getDescriptorForType().getContainingType();
    }
}
