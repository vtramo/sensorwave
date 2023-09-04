package com.sensorwave.iotprocessor.service.sensordata;

public record StatusSensorData(SensorDataType sensorDataType, boolean isOnline) implements SensorData {

    StatusSensorData(boolean isOnline) {
        this(SensorDataType.STATUS, isOnline);
    }

    @Override
    public SensorDataType getSensorDataType() {
        return sensorDataType;
    }
}
