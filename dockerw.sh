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
cd TelegramBot/
echo  "------------------------------"
echo "Creating docker image for TelegramBot"
docker build -t my-bot .
cd ..
cd GasStationsNode/
echo  "------------------------------"
echo "Creating docker image for GasStationsNode"
docker build -t gas-station-node .
else
docker-compose -f docker-bot-local.yaml up -d
fi