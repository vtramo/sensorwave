package com.sensorwave.iotprocessor.service;

import com.sensorwave.iotprocessor.entity.RoomEntity;
import com.sensorwave.iotprocessor.interceptor.LoggedSubscription;
import com.sensorwave.iotprocessor.service.exceptions.InvalidTopicNameException;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.messages.MqttConnAckMessage;
import io.vertx.mqtt.messages.MqttPublishMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class MQTTRoomService extends MQTTAbstractService {

    private static final String topicRoomPattern = "room/[^/]+/smartobject/[^/]+/(message)";

    @Inject
    SmartObjectMessageProcessor smartObjectMessageProcessor;

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
        unsubscribeToRoomIfDeleted(roomId); // TODO: persistent session

        final Buffer payload = mqttPublishMessage.payload();
        final byte[] bytes = payload.getBytes();
        smartObjectMessageProcessor.process(bytes);
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

    public CompletableFuture<Boolean> subscribeToSmartObjectRoom(final String roomId, final String smartObjectId) {
        final CompletableFuture<Boolean> successfullySubscribedFuture = new CompletableFuture<>();

        final String smartObjectRoomTopic = buildSmartObjectRoomMessageTopic(roomId, smartObjectId);
        subscribe(smartObjectRoomTopic).thenAccept(successfullySubscribedFuture::complete);

        return successfullySubscribedFuture;
    }

    @LoggedSubscription
    public CompletableFuture<Boolean> subscribe(final String topic) {
        final CompletableFuture<Boolean> successfullySubscribedFuture = new CompletableFuture<>();
        mqttClient.subscribe(
            topic,
            MqttQoS.AT_LEAST_ONCE.value(),
            subAckReturnCodeAsync -> successfullySubscribedFuture.complete(true) // TODO: handle failure!
        );
        return successfullySubscribedFuture;
    }

    private static String buildSmartObjectRoomMessageTopic(final String roomId, final String smartObjectId) {
        return String.format("room/%s/smartobject/%s/message", roomId, smartObjectId);
    }
}