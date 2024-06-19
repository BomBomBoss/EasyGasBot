ARG PLATFORM=amd64

FROM --platform=linux/${PLATFORM} bellsoft/liberica-openjdk-alpine:21
ARG JAR_FILE=./build/libs/GasStationsNode.jar
COPY . /java_app
CMD ["java", "-jar", "java_app/build/libs/GasStationsNode.jar"]