package com.sensorwave.iotprocessor.service;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Timestamp;
import com.sensorwave.iotprocessor.*;
import com.sensorwave.iotprocessor.clients.GeocoderGraphQLClient;
import com.sensorwave.iotprocessor.config.GeocoderServiceClientConfig;
import com.sensorwave.iotprocessor.entity.SmartObjectMessageEntity;
import com.sensorwave.iotprocessor.mapper.SmartObjectMessageMapper;
import io.quarkus.logging.Log;
import io.smallrye.graphql.client.GraphQLClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;

import java.time.Instant;

@ApplicationScoped
public class SmartObjectMessageProcessor {

    @Inject SmartObjectMessageMapper smartObjectMessageMapper;
    @GraphQLClient(GeocoderServiceClientConfig.NAME)
    GeocoderGraphQLClient geocoderGraphQLClient;

    public void process(final byte[] smartObjectMessagePayload) {
        final SmartObjectMessage smartObjectMessage = parseToSmartObjectMessage(smartObjectMessagePayload);
        process(smartObjectMessage);
    }

    public void process(final SmartObjectMessage smartObjectMessage) {
        Instant instantTimestamp = null;
        if (smartObjectMessage.hasTimestamp()) {
            final Timestamp timestamp = smartObjectMessage.getTimestamp();
            instantTimestamp = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
        }
        final String smartObjectId = smartObjectMessage.getSmartObjectId();
        Log.infof("New smart object message from: %s [%s]", smartObjectId, instantTimestamp);

        persist(smartObjectMessage);
        processData(smartObjectMessage);
    }

    private void persist(final SmartObjectMessage smartObjectMessage) {
        final SmartObjectMessageEntity smartObjectMessageEntity =
            smartObjectMessageMapper.toSmartObjectMessageEntity(smartObjectMessage);
        SmartObjectMessageEntity.persist(smartObjectMessageEntity);
    }

    @SneakyThrows
    private void processData(final SmartObjectMessage smartObjectMessage) {
        for (final Data data: smartObjectMessage.getDataList()) {
            final Any anyData = data.getData();
            switch (data.getType()) {
                case STATUS -> status(smartObjectMessage, anyData.unpack(Status.class));
                case POSITION -> position(smartObjectMessage, anyData.unpack(Position.class));
                case HUMIDITY -> humidity(smartObjectMessage, anyData.unpack(Humidity.class));
                case TEMPERATURE -> temperature(smartObjectMessage, anyData.unpack(Temperature.class));
                case UNRECOGNIZED -> throw new RuntimeException("Unrecognized smart object message!");
            }
        }
    }

    private void status(final SmartObjectMessage smartObjectMessage, final Status status) {
        // TODO
        Log.infof("Smart object %s -> Status: %s", smartObjectMessage.getSmartObjectId(), status);
    }

    private void position(final SmartObjectMessage smartObjectMessage, final Position position) {
        // TODO
        Log.infof("Smart object %s -> Position: %s", smartObjectMessage.getSmartObjectId(), position);
    }

    private void humidity(final SmartObjectMessage smartObjectMessage, final Humidity humidity) {
        // TODO
        Log.infof("Smart object %s -> Humidity: %s", smartObjectMessage.getSmartObjectId(), humidity);
    }

    private void temperature(final SmartObjectMessage smartObjectMessage, final Temperature temperature) {
        // TODO
        Log.infof("Smart object %s -> Temperature: %s", smartObjectMessage.getSmartObjectId(), temperature);
    }

    private static SmartObjectMessage parseToSmartObjectMessage(final byte[] smartObjectMessageBytes) {
        try {
            return SmartObjectMessage.parseFrom(smartObjectMessageBytes);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }
}