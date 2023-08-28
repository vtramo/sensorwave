#!/usr/bin/bash

# Set this env vars
# KEYCLOAK_HOST_ABS_PATH - MOSQUITTO_HOST_ABS_PATH - MONGODB_HOST_ABS_PATH - KIND_CLUSTER_ABS_PATH
set -o allexport
source .env set

tempfile=$(mktemp)
cat ./kind-config.yaml | envsubst > "$tempfile"
kind create cluster --config "$tempfile" --name "$KIND_CLUSTER_NAME"
rm "$tempfile"
