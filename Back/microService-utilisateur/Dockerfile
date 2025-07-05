# Dockerfile
FROM openjdk:21-jdk-slim

# Installe curl dans le conteneur.
# openjdk:21-jdk-slim est basé sur Debian/Ubuntu, donc nous utilisons apt-get.
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

COPY target/medilabo-api-utilisateur.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
