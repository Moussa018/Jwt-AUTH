# Étape 1 : Build
FROM gradle:8.5-jdk17-alpine AS build
WORKDIR /app

# 1. Copier uniquement les fichiers de configuration Gradle
COPY build.gradle settings.gradle ./

# 2. Télécharger les dépendances (cette couche sera mise en cache)
# On lance une compilation à vide pour forcer le téléchargement
RUN gradle build --no-daemon > /dev/null 2>&1 || true

# 3. Copier le code source et builder l'application
COPY src ./src
RUN gradle bootJar --no-daemon

# Étape 2 : Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Sécurité : Création d'un utilisateur non-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copier le JAR final depuis l'étape de build
# On utilise un wildcard pour attraper le nom généré par bootJar
COPY --from=build /app/build/libs/*.jar app.jar

# Configuration de la JVM pour les containers (optimisation mémoire)
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

EXPOSE 8081

# Lancement propre
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]