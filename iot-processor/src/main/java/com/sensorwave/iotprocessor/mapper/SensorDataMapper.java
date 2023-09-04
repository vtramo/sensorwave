package com.sensorwave.iotprocessor.mapper;

import com.google.protobuf.Any;
import com.sensorwave.iotprocessor.*;
import com.sensorwave.iotprocessor.service.sensordata.*;
import lombok.SneakyThrows;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta")
public interface SensorDataMapper {

    @SneakyThrows
    default SensorData toSensorData(Data data) {
        final Any anyData = data.getData();
        return switch (data.getType()) {
            case STATUS -> toStatusSensorData(anyData.unpack(Status.class), SensorData.SensorDataType.STATUS);
            case POSITION -> toPositionSensorData(anyData.unpack(Position.class), SensorData.SensorDataType.POSITION);
            case HUMIDITY -> toHumiditySensorData(anyData.unpack(Humidity.class), SensorData.SensorDataType.HUMIDITY);
            case TEMPERATURE -> toTemperatureSensorData(anyData.unpack(Temperature.class), SensorData.SensorDataType.TEMPERATURE);
            case UNRECOGNIZED -> throw new RuntimeException("Unrecognized smart object message!");
        };
    }

    @Mapping(target = "sensorDataType", expression = "java(sensorDataType)")
    TemperatureSensorData toTemperatureSensorData(Temperature temperature, SensorData.SensorDataType sensorDataType);

    @Mapping(target = "sensorDataType", expression = "java(sensorDataType)")
    StatusSensorData toStatusSensorData(Status status, SensorData.SensorDataType sensorDataType);

    @Mapping(target = "sensorDataType", expression = "java(sensorDataType)")
    HumiditySensorData toHumiditySensorData(Humidity humidity, SensorData.SensorDataType sensorDataType);

    default PositionSensorData toPositionSensorData(Position position, SensorData.SensorDataType sensorDataType) {
        final double longitude = position.getLongitude();
        final double latitude = position.getLatitude();
        return new PositionSensorData(sensorDataType, PositionSensorData.GeoJsonPoint.toGeoJsonPoint(longitude, latitude));
    }
}
