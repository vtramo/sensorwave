package com.sensorwave.iotprocessor.service.sensordata;

public record HumiditySensorData(SensorDataType sensorDataType, double humidity) implements SensorData {

    HumiditySensorData(double humidity) {
        this(SensorDataType.HUMIDITY, humidity);
    }

    @Override
    public SensorDataType getSensorDataType() {
        return sensorDataType;
    }
}
