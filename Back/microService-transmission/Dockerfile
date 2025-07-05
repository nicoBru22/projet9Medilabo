# Dockerfile
FROM openjdk:21-jdk-slim
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
COPY target/medilabo-api-transmission.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
