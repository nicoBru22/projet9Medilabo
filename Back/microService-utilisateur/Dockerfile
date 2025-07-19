# Dockerfile API-Utilisateur
FROM openjdk:21-jdk-slim

# Installe curl et les certificats racine (nécessaires pour MongoDB Atlas en TLS)
RUN apt-get update && apt-get install -y curl ca-certificates && rm -rf /var/lib/apt/lists/*

# Copie le JAR compilé dans l'image
COPY target/medilabo-api-utilisateur.jar app.jar

# Lancement en forçant TLS 1.2 (utile si tu te connectes à MongoDB Atlas)
ENTRYPOINT ["java", "-Djdk.tls.client.protocols=TLSv1.2", "-jar", "/app.jar"]

