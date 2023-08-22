package org.github.rmiot.service;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.quarkus.logging.Log;
import io.smallrye.graphql.client.GraphQLClient;
import io.vertx.mqtt.messages.MqttConnAckMessage;
import io.vertx.mqtt.messages.MqttPublishMessage;
import jakarta.enterprise.context.ApplicationScoped;
import org.github.rmiot.clients.GeocoderGraphQLClient;
import org.github.rmiot.clients.ReverseGeocodingResult;
import org.github.rmiot.config.GeocoderServiceClientConfig;
import org.github.rmiot.entity.RoomEntity;
import org.github.rmiot.interceptor.LoggedRoomSubscription;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class MQTTRoomService extends MQTTAbstractService {

    final String topicRoomPattern = "room/(\\d+)/smartobject/\\d+/(message|status)";

    @GraphQLClient(GeocoderServiceClientConfig.NAME)
    GeocoderGraphQLClient geocoderGraphQLClient;

    @Override
    void handleMqttConnAckMessage(final MqttConnAckMessage mqttConnAckMessage) {
        super.handleMqttConnAckMessage(mqttConnAckMessage);
    }

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