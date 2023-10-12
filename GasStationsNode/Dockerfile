FROM bellsoft/liberica-openjdk-alpine:17
ARG JAR_FILE=./build/libs/GasStationsNode.jar
COPY $JAR_FILE /java_app
CMD ["java", "-jar", "java_app/build/libs/GasStationsNode.jar"]