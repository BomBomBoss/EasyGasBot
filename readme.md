### Telegram Bot which shows cheapest fuel prices in Latvia

## Modules:
- _TelegramBot_ - responsible for handling user's request/response, bot registration on Telegram's servers, commands setting (bot menu button)
- _RabbitMQ_ - message broker which located between TelegramBot and GasStationNode modules
- _GasStationNode_ - main service which pulls data (every 1 hour) from external sources, persisting/retrieving data from DB, sorting prices
- _Liquibase_ - responsible for correct data migration to DB (Postgresql)
- _Common-utils_ - common library for TelegramBot and GasStationNode modules. Text translations located here

## Docker-compose:
- _docker-bot.yaml_ - to up all modules in containers
- _docker-bot-local.yaml_ - to run only RabbitMQ and DB in containers. For local testing

## Script:
_./dockerw.sh_ - script with arguments to build jar, create image and run containers
_./dockerw.sh push gas_ - to push station node to docker hub
_./dockerw.sh push bot_ - to push bot node to docker hub

