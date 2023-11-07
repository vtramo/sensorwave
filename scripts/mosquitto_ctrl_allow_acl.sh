#!/bin/bash

set -e -u -o pipefail
source .env

mosquitto_ctrl_acl () {
  local ACL=$1
  local ALLOW=$2
  mosquitto_ctrl -h "${MOSQUITTO_HOST}" \
    -p "${MOSQUITTO_PORT}" \
    -u "${MOSQUITTO_ADMIN_USERNAME}" \
    -P "${MOSQUITTO_ADMIN_PASSWORD}" \
    -d dynsec setDefaultACLAccess "${ACL}" "${ALLOW}"
}

mosquitto_ctrl_acl publishClientSend allow
mosquitto_ctrl_acl publishClientReceive allow
mosquitto_ctrl_acl subscribe allow
mosquitto_ctrl_acl unsubscribe allow
