package com.sensorwave.iotprocessor.service.sensordata;

public record TemperatureSensorData(SensorDataType sensorDataType, double temperature) implements SensorData {

    TemperatureSensorData(double temperature) {
        this(SensorDataType.TEMPERATURE, temperature);
    }

    @Override
    public SensorDataType getSensorDataType() {
        return sensorDataType;
    }
}
