FROM openjdk:17-jdk-slim

WORKDIR /app

RUN apt-get update && apt-get install -y curl

COPY src/main/resources/spreadsheets/swift_codes.xlsx /app/spreadsheets/
COPY src/main/resources/db /app/db/

COPY target/swift-0.0.1.jar /app/swift-0.0.1.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/swift-0.0.1.jar"]