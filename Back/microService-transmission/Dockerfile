# Dockerfile API-Transmission
FROM openjdk:21-jdk-slim

# Installation de curl + certificats SSL
RUN apt-get update && apt-get install -y curl ca-certificates && rm -rf /var/lib/apt/lists/*

# Copie du JAR
COPY target/medilabo-api-transmission.jar app.jar

# Lancement avec support TLS 1.2 (utile pour MongoDB Atlas)
ENTRYPOINT ["java", "-Djdk.tls.client.protocols=TLSv1.2", "-jar", "/app.jar"]

