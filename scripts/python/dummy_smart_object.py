#!/usr/bin/python3

import paho.mqtt.publish as publish
import paho.mqtt.client as mqtt
import iot_processor_pb2

smart_object_message = iot_processor_pb2.SmartObjectMessage()
smart_object_message.timestamp.GetCurrentTime()

data = [iot_processor_pb2.Data(), iot_processor_pb2.Data(), iot_processor_pb2.Data()]


data[0].type = iot_processor_pb2.STATUS
status = iot_processor_pb2.Status()
status.isOnline = True
data[0].data.Pack(status)

data[1].type = iot_processor_pb2.TEMPERATURE
temperature = iot_processor_pb2.Temperature()
temperature.value = 5.0
data[1].data.Pack(temperature)

data[2].type = iot_processor_pb2.POSITION
position = iot_processor_pb2.Position()
position.latitude = 5.0
position.longitude = 6.0
data[2].data.Pack(position)

smart_object_message.data.append(data[0])
smart_object_message.data.append(data[1])
smart_object_message.data.append(data[2])

broker = 'localhost'
port = 32777
topic = "room/64efbd22b7ddad277f4a422d/smartobject/5935d7f3-b309-4a24-b3f3-cdb302bbf5a1/message"
username="vtramo250"
password="ciao"

def on_message(client, userdata, msg):
    print(msg.topic+" "+str(msg.payload))

def on_connect(client, userdata, flags, rc):
    print("Connected with result code "+str(rc))

    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.
    client.subscribe("$SYS/#")
    client.publish(topic=topic, qos=1, payload=smart_object_message.SerializeToString())

def on_publish(client, userdata, result):
    print("data published \n")
    print(userdata)
    print(result)
    pass

if __name__ == '__main__':
    client = mqtt.Client(client_id=username)
    client.on_message = on_message
    client.on_connect = on_connect
    client.on_publish = on_publish

    client.username_pw_set(username=username, password=password)
    client.connect(broker, port)


    client.loop_forever()
