#!/bin/bash

registry_url=${REGISTRY_URL:-localhost:5000}

read_image_tag() {
	local image_name=$1
	echo -n "$image_name TAG: "
	read -r image_tag
	image_tag="$image_tag"
}

ask_confirm_push() {
	local image_name=$1
	full_image_name="$registry_url/sensorwave/$image_name:$image_tag"
  echo -n "I am going to push the image $full_image_name. Proceed? [y/n]: "
  read -r ans
	[ "$ans" = "y" ] && return 0 || return 1
}

docker_build_and_push() {
	local image_name=$1

  docker_file_path=""
  if [[ "$image_name" == "mosquitto" ]]; then
    docker_file_path="../mosquitto/Dockerfile"
  else
    docker_file_path="../$image_name/src/main/docker/Dockerfile.jvm"
  fi

	docker buildx build \
    --push --quiet -t "$registry_url/sensorwave/$image_name:$image_tag" \
    -f "$docker_file_path" "../$image_name" \
		&& echo -e "Image $full_image_name pushed."
}


services=(${*:-iot-processor iot-security geocoder mosquitto})

for image_name in "${services[@]}"; do
  read_image_tag "$image_name"

  ask_confirm_push "$image_name" && \
    docker_build_and_push "$image_name" || echo -e "Cancelled."

  echo -e "\n"
done			

exit 0