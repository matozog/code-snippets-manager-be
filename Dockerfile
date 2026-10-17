
FROM eclipse-temurin:21-jdk-noble

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY db.mv.db ./db.mv.db

RUN chmod +x ./mvnw
RUN ./mvnw dependency:resolve

COPY src ./src

CMD ["./mvnw", "spring-boot:run"]

EXPOSE 8090