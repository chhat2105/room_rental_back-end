# ---------- Stage 1: Build ----------
FROM gradle:8.10-jdk17 AS build
WORKDIR /app

# Copy Gradle config first (better layer caching for dependencies)
COPY build.gradle settings.gradle ./
COPY src ./src

RUN gradle build -x test --no-daemon

# ---------- Stage 2: Run ----------
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
