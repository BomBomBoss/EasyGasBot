FROM --platform=linux/amd64 bellsoft/liberica-openjdk-alpine:25
#FROM bellsoft/liberica-openjdk-alpine:21
ARG JAR_FILE=target/*.jar
COPY . /java_app
CMD ["java", "-jar", "java_app/build/libs/TelegramBot.jar"]