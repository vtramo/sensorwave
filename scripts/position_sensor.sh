#!/bin/bash

topic=''
qos=1
broker_host='localhost'
broker_port='32777'

while :; do
	latitude=$(seq -90 .01 90 | shuf | head -n1 | sed 's/,/./g')
	longitude=$(seq -180 .01 180 | shuf | head -n1 | sed 's/,/./g')
	json_position="{ \"latitude\": $latitude, \"longitude\": $longitude }"

	mosquitto_pub -h "$broker_host" -p "$broker_port" -t "$topic" -q "$qos" -m "$json_position"

	sleep 1
done
