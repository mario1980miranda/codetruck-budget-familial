# Étape 1 : compilation du jar avec le wrapper Maven (tests ignorés : ils
# ont besoin d'une base de données, et ils sont lancés par ./mvnw verify en dev).
FROM eclipse-temurin:25-jdk AS construction
WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw -q dependency:go-offline
COPY src src
RUN ./mvnw -q -DskipTests package

# Étape 2 : image d'exécution légère. curl sert au healthcheck de docker-compose.
FROM eclipse-temurin:25-jre
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=construction /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
