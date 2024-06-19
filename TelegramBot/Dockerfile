ARG PLATFORM=amd64

FROM --platform=linux/${PLATFORM} bellsoft/liberica-openjdk-alpine:21
ARG JAR_FILE=target/*.jar
COPY . /java_app
CMD ["java", "-jar", "java_app/build/libs/TelegramBot.jar"]