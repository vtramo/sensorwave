package com.sensorwave.iotprocessor.service;

import com.google.protobuf.Any;
import com.google.protobuf.Timestamp;
import com.sensorwave.iotprocessor.*;
import com.sensorwave.iotprocessor.clients.GeocoderGraphQLClient;
import com.sensorwave.iotprocessor.clients.ReverseGeocodingResult;
import com.sensorwave.iotprocessor.config.GeocoderServiceClientConfig;
import com.sensorwave.iotprocessor.entity.RoomEntity;
import com.sensorwave.iotprocessor.interceptor.LoggedRoomSubscription;
import com.sensorwave.iotprocessor.service.exceptions.InvalidTopicNameException;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.quarkus.logging.Log;
import io.smallrye.graphql.client.GraphQLClient;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.messages.MqttConnAckMessage;
import io.vertx.mqtt.messages.MqttPublishMessage;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.SneakyThrows;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class MQTTRoomService extends MQTTAbstractService {

    final String topicRoomPattern = "room/[^/]+/smartobject/[^/]+/(message|status)";

    @GraphQLClient(GeocoderServiceClientConfig.NAME)
    GeocoderGraphQLClient geocoderGraphQLClient;

    @Override
    void handleMqttConnAckMessage(final MqttConnAckMessage mqttConnAckMessage) {
        super.handleMqttConnAckMessage(mqttConnAckMessage);
    }

    @SneakyThrows
    @Override
    void handleMqttPublishMessage(final MqttPublishMessage mqttPublishMessage) {
        super.handleMqttPublishMessage(mqttPublishMessage);
        
        final String topicName = mqttPublishMessage.topicName();
        if (!isValidTopicName(topicName)) {
            throw new InvalidTopicNameException("The topic name " + topicName + " does not match any valid pattern!");
        }

        final String roomId = extractRoomIdFromTopicName(topicName);
        final String smartObjectId = extractSmartObjectIdFromTopicName(topicName);
        unsubscribeToRoomIfDeleted(roomId);

        final Buffer payload = mqttPublishMessage.payload();
        final byte[] bytes = payload.getBytes();
        final SmartObjectMessage smartObjectMessage = SmartObjectMessage.parseFrom(bytes);
        final Timestamp timestamp = smartObjectMessage.getTimestamp();
        Log.infof("%s timestamp: %s", smartObjectId, timestamp);
        for (final Data data: smartObjectMessage.getDataList()) {
            final Any anyData = data.getData();
            switch (data.getType()) {
                case STATUS -> Log.infof("%s STATUS message: %s", smartObjectId, anyData.unpack(Status.class));
                case POSITION -> Log.infof("%s POSITION message: %s", smartObjectId, anyData.unpack(Position.class));
                case HUMIDITY -> Log.infof("%s HUMIDITY message: %s", smartObjectId, anyData.unpack(Humidity.class));
                case TEMPERATURE -> Log.infof("%s TEMPERATURE message: %s", smartObjectId, anyData.unpack(Temperature.class));
                case UNRECOGNIZED -> Log.warnf("%s UNRECOGNIZED message", smartObjectId);
            }
        }

        // TODO: Logic for parse messages (standard)
/*        final Position position = Json.decodeValue(mqttPublishMessage.payload(), Position.class);
        geocoderGraphQLClient.reverseGeocoding(position)
            .subscribe()
            .with(this::onReverseGeocodingResult);*/
    }

    private boolean isValidTopicName(final String topicName) {
        final Pattern pattern = Pattern.compile(topicRoomPattern);
        final Matcher matcher = pattern.matcher(topicName);
        return matcher.matches();
    }

    private String extractRoomIdFromTopicName(final String topicName) {
        final String[] tokens = topicName.split("/");
        return tokens[1];
    }

    private String extractSmartObjectIdFromTopicName(final String topicName) {
        final String[] tokens = topicName.split("/");
        return tokens[3];
    }

    private void unsubscribeToRoomIfDeleted(final String roomId) {
        final Optional<RoomEntity> optionalRoomEntity = RoomEntity.findByIdOptional(roomId);
        if (optionalRoomEntity.isPresent()) return;
        mqttClient.unsubscribe("room/" + roomId + "/#");
    }

    private void onReverseGeocodingResult(ReverseGeocodingResult reverseGeocodingResult) {
        Log.info("Name: " + reverseGeocodingResult.getData().get(0).name);
    }

    @LoggedRoomSubscription
    public CompletableFuture<Boolean> subscribeToRoom(final String roomId) {
        final CompletableFuture<Boolean> successfullySubscribedFuture = new CompletableFuture<>();

        mqttClient.subscribe(
            "room/" + roomId + "/#",
            MqttQoS.AT_LEAST_ONCE.value(),
            subAckReturnCodeAsync -> {
                final int subAckReturnCode = subAckReturnCodeAsync.result();
                final boolean successfullySubscribed = (subAckReturnCode == MqttQoS.AT_LEAST_ONCE.value());
                successfullySubscribedFuture.complete(successfullySubscribed);
            }
        );

        return successfullySubscribedFuture;
    }
}