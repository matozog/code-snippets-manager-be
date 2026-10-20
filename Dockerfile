# Build stage: compiles the application into an executable jar.
# Tests are not run here because they need Docker (Testcontainers); CI runs them.
FROM eclipse-temurin:21-jdk-noble AS build

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw && ./mvnw -B -q dependency:go-offline

COPY src ./src
RUN ./mvnw -B -q package -DskipTests

# Runtime stage: only the JRE and the jar, started directly without Maven.
FROM eclipse-temurin:21-jre-noble

WORKDIR /app

RUN groupadd --system app && useradd --system --gid app app
COPY --from=build /app/target/*.jar app.jar
USER app

EXPOSE 8090

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
