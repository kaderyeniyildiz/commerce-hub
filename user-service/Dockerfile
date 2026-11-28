
# ==========================================
# Stage 1: Build
# ==========================================
FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

# Copy the entire multi-module project
COPY . .

# Build user-service and required Maven modules
RUN mvn -pl user-service -am clean package -DskipTests

# Compile Docker healthcheck
RUN mkdir -p /app/healthcheck && \
    javac -d /app/healthcheck \
    user-service/docker/healthcheck/Healthcheck.java && \
    find /app/healthcheck -type f

# ==========================================
# Stage 2: Runtime
# ==========================================
FROM eclipse-temurin:25-jre

WORKDIR /app

# Create non-root user
RUN useradd --system --create-home spring

# Copy the generated Spring Boot JAR
COPY --from=build /app/user-service/target/*.jar app.jar

# Copy healthcheck class
COPY --from=build /app/healthcheck /app/healthcheck

# Run application as non-root user
USER spring

# Start Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
