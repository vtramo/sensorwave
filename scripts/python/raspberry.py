import os
import paho.mqtt.client as mqtt
import iot_processor_pb2
import time
import glob

# Sensorwave env var names
MQTT_HOST_ENV_VAR_NAME='SENSORWAVE_MQTT_HOST'
MQTT_PORT_ENV_VAR_NAME='SENSORWAVE_MQTT_PORT'
MQTT_USERNAME_ENV_VAR_NAME='SENSORWAVE_MQTT_USERNAME'
MQTT_PASSWORD_ENV_VAR_NAME='SENSORWAVE_MQTT_PASSWORD'
ROOM_ID_ENV_VAR_NAME='SENSORWAVE_ROOM_ID'
SMART_OBJECT_ID_ENV_VAR_NAME='SENSORWAVE_SMARTOBJECT_ID'
INITIAL_LATITUDE_ENV_VAR_NAME= 'SENSORWAVE_LATITUDE'
INITIAL_LONGITUDE_ENV_VAR_NAME='SENSORWAVE_LONGITUDE'

# Devices env var names
BASE_DIR_DEVICES_ENV_VAR_NAME='BASE_DIR_DEVICES'
DEVICE_FILE_ENV_VAR_NAME='DEVICE_FILE'

global message_topic
global room_id
global smartobject_id
global device_file
global mqtt_client_is_connected

def build_topic_from_room_smartobject_id(room_id: str, smartobject_id: str) -> str:
    return f"room/{room_id}/smartobject/{smartobject_id}/message"

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

def build_device_file_absolute_path() -> str:
    return f"{os.environ[BASE_DIR_DEVICES_ENV_VAR_NAME]}/{os.environ[DEVICE_FILE_ENV_VAR_NAME]}"

def read_temp_raw() -> list[str]:
    f = open(device_file, 'r')
    lines = f.readlines()
    f.close()
    return lines

def read_temp() -> tuple[float, float]:
    lines = read_temp_raw()
    while lines[0].strip()[-3:] != 'YES':
        time.sleep(0.2)
        lines = read_temp_raw()
    equals_pos = lines[1].find('t=')
    if equals_pos != -1:
        temp_string = lines[1][equals_pos+2:]
        temp_c = float(temp_string) / 1000.0
        temp_f = temp_c * 9.0 / 5.0 + 32.0
        return temp_c, temp_f

def on_connect(client, userdata, flags, rc):
    global mqtt_client_is_connected
    mqtt_client_is_connected = True

def on_publish(client, userdata, result):
    t = time.localtime()
    current_time = time.strftime("%H:%M:%S", t)
    print(f"[{current_time}] Publish message {userdata}. Message number {result}.")

if __name__ == '__main__':
    mqtt_host = os.environ[MQTT_HOST_ENV_VAR_NAME].replace('\'', '')
    mqtt_port = int(os.environ[MQTT_PORT_ENV_VAR_NAME])
    mqtt_username = os.environ[MQTT_USERNAME_ENV_VAR_NAME].replace('\'', '')
    mqtt_password = os.environ[MQTT_PASSWORD_ENV_VAR_NAME].replace('\'', '')
    room_id = os.environ[ROOM_ID_ENV_VAR_NAME].replace('\'', '')
    smartobject_id = os.environ[SMART_OBJECT_ID_ENV_VAR_NAME].replace('\'', '')
    latitude=float(os.environ[INITIAL_LATITUDE_ENV_VAR_NAME].replace('\'', ''))
    longitude=float(os.environ[INITIAL_LONGITUDE_ENV_VAR_NAME].replace('\'', ''))

    message_topic = build_topic_from_room_smartobject_id(room_id, smartobject_id)
    device_file = build_device_file_absolute_path()

    smart_object_message_will = build_smart_object_message(sensor_data=[
        build_status_data(is_online=False)
    ], room_id=room_id, smart_object_id=smartobject_id, timestamp=False)

    mqtt_client = mqtt.Client(client_id=mqtt_username)
    mqtt_client.on_connect = on_connect
    mqtt_client.on_publish = on_publish
    mqtt_client.username_pw_set(username=mqtt_username, password=mqtt_password)
    mqtt_client.will_set(topic=message_topic, payload=smart_object_message_will.SerializeToString(), qos=1, retain=True)
    mqtt_client_is_connected = False
    mqtt_client.connect(mqtt_host, mqtt_port)
    mqtt_client.loop_start()

    while not mqtt_client_is_connected:
        print("Waiting for MQTT connection ...")
        time.sleep(1)

    while True:
        temperature_celsius, _ = read_temp()
        smart_object_message = build_smart_object_message(sensor_data=[
            build_status_data(is_online=True),
            build_temperature_data(temperature=temperature_celsius),
            build_position_data(position=(latitude, longitude))
        ], room_id=room_id, smart_object_id=smartobject_id)
        mqtt_client.publish(topic=message_topic, qos=1, payload=smart_object_message.SerializeToString())
        time.sleep(1)