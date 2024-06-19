#!/bin/bash

if [ "$1" = "full" ]
then docker-compose -f docker-bot.yaml up -d
elif [ "$1" = "down"  ]; then
docker-compose -f docker-bot.yaml down
elif [ "$1" = "create"  ]; then
  echo  "------------------------------"
  echo "Building bootJar for TelegramBot"
  ./gradlew TelegramBot:bootJar
  echo  "------------------------------"
  echo "Building bootJar for GasStationsNode"
  ./gradlew GasStationsNode:bootJar
  cd GasStationsNode/
  echo  "------------------------------"
  if [ "$2" = "mac" ]; then
    echo "Creating docker image for GasStationsNode for $2"
    docker build --build-arg="PLATFORM=arm64" -t gas-station-node .
    cd ..
    cd TelegramBot/
    echo  "------------------------------"
    echo "Creating docker image for TelegramBot for $2"
    docker build --build-arg="PLATFORM=arm64" -t my-bot .
  else
  echo "Creating docker image for GasStationsNode"
  docker build -t gas-station-node .
  cd ..
  cd TelegramBot/
  echo  "------------------------------"
  echo "Creating docker image for TelegramBot"
  docker build -t my-bot .
  fi
else
docker-compose -f docker-bot-local.yaml up -d
fi