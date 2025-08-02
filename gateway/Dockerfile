# Dockerfile
FROM openjdk:21-jdk-slim
COPY target/medilabo-api-gateway.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]