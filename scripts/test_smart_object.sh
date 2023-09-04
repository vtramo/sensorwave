#!/bin/bash

username='vtramo123442224252222522'
password='ciao'
iot_security_host='localhost:8092'
iot_processor_host='localhost:8090'
#keycloak_host='localhost:8093'
keycloak_host='localhost:32778'
#keycloak_realm='app'
keycloak_realm='quarkus'

# Register user
curl -s -X POST "http://${iot_security_host}/api/iot-security/users/register" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=${username}&password=${password}" || exit 1

# Obtain an access token
#client_id='ext-client'
#client_secret='28l5hqqBmi84WUQTD4XX0ABLrMhsReQy'
client_id='quarkus-dev'
client_secret='GmOSW7iOxcZoG6ESp9nuNZuhuHqds1AX'

access_token=$(curl -s -X POST "http://${keycloak_host}/realms/${keycloak_realm}/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=${client_id}&client_secret=${client_secret}&grant_type=password&username=${username}&password=${password}&scope=openid" \
  | jq .access_token | sed 's/"//g') || exit 1

# Create a room
room_name='room'

room_id=$(curl -s -X POST "http://${iot_processor_host}/api/iot-processor/rooms" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${access_token}" \
  -d "{ \"name\": \"${room_name}\" }" | jq .id | sed 's/"//g') || exit 1

# Create a smart object
smart_object_name='my-object'

smart_object_id=$(curl -s -X POST "http://${iot_processor_host}/api/iot-processor/rooms/${room_name}/smartobjects" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${access_token}" \
  -d "{ \"name\": \"${smart_object_name}\", \"roomOwnerUsername\": \"${username}\" }" | jq .id | sed 's/"//g') || exit 1

# Send data
sleep 0.5
mqtt_broker='localhost'
#mqtt_broker_port='1884'
mqtt_broker_port='32777'
echo -e "\n"
/home/vincenzo/projects/sensorwave/scripts/python/dummy_smart_object.py \
  "${mqtt_broker}" \
  "${mqtt_broker_port}" \
  "${room_id}" \
  "${smart_object_id}" \
  "${username}" \
  "${password}"