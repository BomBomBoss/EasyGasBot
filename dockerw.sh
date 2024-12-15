#!/bin/bash

GAS_STATION_NODE_IMAGE="gas-station-node"
NODE_REP_TAG="vladjuha13/station-service"

BOT_IMAGE="my-bot"
BOT_REP_TAG="vladjuha13/easygas"

if [ "$1" = "full" ]
then docker-compose -f docker-bot.yaml up -d
elif [ "$1" = "down"  ]; then
docker-compose -f docker-bot.yaml down
elif [ "$1" = "create"  ]; then
echo  "------------------------------"
echo -e "\033[0;32mBuilding bootJar for TelegramBot\033[0m"
./gradlew TelegramBot:bootJar
echo  "------------------------------"
echo "Building bootJar for GasStationsNode"
./gradlew GasStationsNode:bootJar
cd TelegramBot/
echo  "------------------------------"
echo "Creating docker image for TelegramBot"
docker build -t my-bot .
cd ..
cd GasStationsNode/
echo  "------------------------------"
echo "Creating docker image for GasStationsNode"
docker build -t gas-station-node .
# creating and pushing image to hub
elif [ "$1" = "push" ]; then
  echo "login into docker hub ... "

 # Login to Docker Hub if needed
  if ! docker info > /dev/null 2>&1; then
  echo "Docker not logged in. Performing login..."
  docker login -u "${DOCKER_LOGIN}" -p "${DOCKER_PASSWORD}"
  else
    echo "Already logged in to Docker."
  fi
    if [ "$2" = "gas" ]; then

    echo  "------------------------------"
    echo "Building bootJar for GasStationsNode"
    ./gradlew GasStationsNode:bootJar

    echo "Building Docker image for GasStationsNode..."
    docker build -t ${GAS_STATION_NODE_IMAGE} ./GasStationsNode/

    echo "Tagging Docker image..."
    docker tag ${GAS_STATION_NODE_IMAGE} ${NODE_REP_TAG}

    echo "Pushing Docker image to repository..."
    docker push ${NODE_REP_TAG}

    elif [ "$2" = "bot"  ]; then
       echo  "------------------------------"
        echo "Building bootJar for TelegramBot"
        ./gradlew TelegramBot:bootJar

        echo "Building Docker image for Telegrambot..."
        docker build -t ${BOT_IMAGE} ./TelegramBot/

        echo "Tagging Docker image..."
        docker tag ${BOT_IMAGE} ${BOT_REP_TAG}

        echo "Pushing Docker image to repository..."
        docker push ${BOT_REP_TAG}
    fi
echo -e "\033[0;35mDocker image pushed successfully!\033[0m"
else
docker-compose -f docker-bot-local.yaml up -d
fi

