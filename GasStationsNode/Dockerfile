FROM bellsoft/liberica-openjdk-alpine:17
ARG JAR_FILE=./build/libs/GasStationsNode.jar
COPY . /java_app
CMD ["java", "-jar", "java_app/build/libs/GasStationsNode.jar"]