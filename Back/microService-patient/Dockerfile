# Dockerfile API-Patient
FROM openjdk:21-jdk-slim

# Mise à jour + installation curl et ca-certificates (pour SSL)
RUN apt-get update && \
    apt-get install -y curl ca-certificates && \
    rm -rf /var/lib/apt/lists/*

# Copie du jar
COPY target/medilabo-api-patient.jar app.jar

# Lancement de l'appli avec TLS 1.2 forcé
ENTRYPOINT ["java", "-Djdk.tls.client.protocols=TLSv1.2", "-jar", "/app.jar"]

