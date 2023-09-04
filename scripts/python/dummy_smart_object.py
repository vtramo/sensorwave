#!/usr/bin/python3

import sys

import paho.mqtt.client as mqtt

import iot_processor_pb2


def build_smart_object_message(sensor_data: list[iot_processor_pb2.Data],
                               room_id: str,
                               smart_object_id: str,
                               timestamp: bool = True) -> iot_processor_pb2.SmartObjectMessage:
    smart_object_message = iot_processor_pb2.SmartObjectMessage()
    if timestamp:
        smart_object_message.timestamp.GetCurrentTime()
    smart_object_message.roomId = room_id
    smart_object_message.smartObjectId = smart_object_id
    [smart_object_message.data.append(data) for data in sensor_data]
    return smart_object_message


def build_status_data(is_online: bool = False) -> iot_processor_pb2.Data:
    status_data = iot_processor_pb2.Data()
    status = iot_processor_pb2.Status()
    status.isOnline = is_online
    status_data.type = iot_processor_pb2.STATUS
    status_data.data.Pack(status)
    return status_data


def build_temperature_data(temperature: float) -> iot_processor_pb2.Data:
    temperature_data = iot_processor_pb2.Data()
    temperature_obj = iot_processor_pb2.Temperature()
    temperature_obj.temperature = temperature
    temperature_data.type = iot_processor_pb2.TEMPERATURE
    temperature_data.data.Pack(temperature_obj)
    return temperature_data


def build_position_data(position: tuple[float, float]) -> iot_processor_pb2.Data:
    position_data = iot_processor_pb2.Data()
    position_obj = iot_processor_pb2.Position()
    position_obj.longitude = position[0]
    position_obj.latitude = position[1]
    position_data.type = iot_processor_pb2.POSITION
    position_data.data.Pack(position_obj)
    return position_data


def build_humidity_data(humidity: float) -> iot_processor_pb2.Data:
    if not 0 <= humidity <= 1:
        raise ValueError(f"Bad humidity value ({humidity})")
    humidity_data = iot_processor_pb2.Data()
    humidity_obj = iot_processor_pb2.Humidity()
    humidity_obj.humidity = humidity
    humidity_data.type = iot_processor_pb2.HUMIDITY
    humidity_data.data.Pack(humidity_obj)
    return humidity_data


def on_connect(client, userdata, flags, rc):
    print(f"Connected with result code {rc}.")
    print(f"Topic {topic}.")
    print(f"Broker {broker}.")

    smart_object_message = build_smart_object_message(sensor_data=[
        build_status_data(is_online=True),
        build_temperature_data(temperature=4.0),
        build_position_data(position=(113.0, -78.0)),
        build_humidity_data(humidity=0.7)
    ], room_id=room_id, smart_object_id=smartobjectid)

    client.publish(topic=topic, qos=1, payload=smart_object_message.SerializeToString())


def on_publish(client, userdata, result):
    print("Data published.\n")


if __name__ == '__main__':
    broker = str(sys.argv[1])
    port = int(sys.argv[2])
    room_id = sys.argv[3]
    smartobjectid = sys.argv[4]
    topic = f"room/{room_id}/smartobject/{smartobjectid}/message"
    username = str(sys.argv[5])
    password = str(sys.argv[6])

    smart_object_message_will = build_smart_object_message(sensor_data=[
        build_status_data(is_online=False)
    ], room_id=room_id, smart_object_id=smartobjectid, timestamp=False)

    client = mqtt.Client(client_id=username)
    client.on_connect = on_connect
    client.on_publish = on_publish
    client.username_pw_set(username=username, password=password)
    client.will_set(topic=topic, payload=smart_object_message_will.SerializeToString(), qos=1, retain=True)
    client.connect(broker, port)
    client.loop_forever()
