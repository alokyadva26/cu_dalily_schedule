# Multi-stage build for CU Daily Schedule (Vaadin Flow + Spring Boot)
FROM node:20-bookworm-slim AS nodejs-env

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Provide Node.js 20 & npm in build stage
COPY --from=nodejs-env /usr/local/bin /usr/local/bin
COPY --from=nodejs-env /usr/local/lib/node_modules /usr/local/lib/node_modules

# Copy build files and source code including precompiled bundles
COPY pom.xml package*.json* tsconfig*.json* types.d.ts* ./
COPY src src

# Build production executable JAR using pre-compiled prod bundle
ENV MAVEN_OPTS="-Xmx768m"
RUN mvn clean package -Pproduction -DskipTests

# Lean runtime image (pure Java 21 JRE)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/target/cu-daily-schedule-java-1.0.0.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-Dvaadin.productionMode=true", "-jar", "app.jar"]
