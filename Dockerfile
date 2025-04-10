FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/swift-0.0.1.jar /app/swift-0.0.1.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/swift-0.0.1.jar"]