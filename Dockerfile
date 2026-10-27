# Multi-stage build for CU Daily Schedule (Vaadin Flow + Spring Boot)
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy Maven POM and complete source tree including pre-compiled bundles
COPY pom.xml .
COPY src src

# Package production executable JAR (uses pre-compiled prod.bundle)
RUN mvn clean package -DskipTests

# Lean runtime image (pure Java 21 JRE)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/target/cu-daily-schedule-java-1.0.0.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-Dvaadin.productionMode=true", "-jar", "app.jar"]
