FROM bellsoft/liberica-openjdk-alpine:17
ARG JAR_FILE=target/*.jar
COPY . /java_app
CMD ["java", "-jar", "java_app/build/libs/TelegramBot.jar"]