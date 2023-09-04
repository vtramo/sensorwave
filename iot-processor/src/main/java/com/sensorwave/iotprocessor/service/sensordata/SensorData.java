package com.sensorwave.iotprocessor.service.sensordata;

public sealed interface SensorData
    permits
        TemperatureSensorData,
        PositionSensorData,
        HumiditySensorData,
        StatusSensorData {

    enum SensorDataType { TEMPERATURE, POSITION, HUMIDITY, STATUS }

    SensorDataType getSensorDataType();
}
