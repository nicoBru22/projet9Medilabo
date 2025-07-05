# Dockerfile API-Anticipation
FROM openjdk:21-jdk-slim

# Installe curl dans le conteneur.
# openjdk:21-jdk-slim est basé sur Debian/Ubuntu, donc nous utilisons apt-get.
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copie le fichier JAR de votre application dans le conteneur
COPY target/medilabo-api-anticipation.jar app.jar

# Définit la commande pour exécuter votre application au démarrage du conteneur
ENTRYPOINT ["java", "-jar", "/app.jar"]