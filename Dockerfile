# Multi-stage build for CU Daily Schedule (Vaadin Flow + Spring Boot)
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Install Maven & Node.js for Vaadin frontend compilation
RUN apt-get update && apt-get install -y maven nodejs npm && rm -rf /var/lib/apt/lists/*

COPY pom.xml .
COPY src src

# Build production executable JAR with compiled frontend bundle
RUN mvn clean package -Pproduction -DskipTests

# Runtime image
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/target/cu-daily-schedule-java-1.0.0.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-Dvaadin.productionMode=true", "-jar", "app.jar"]

