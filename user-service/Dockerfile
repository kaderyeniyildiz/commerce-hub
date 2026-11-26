
# ==========================================
# Stage 1: Build
# ==========================================
FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

# Copy the entire multi-module project
COPY . .

# Build user-service and required Maven modules
RUN mvn -pl user-service -am clean package -DskipTests


# ==========================================
# Stage 2: Runtime
# ==========================================
FROM eclipse-temurin:25-jre

# Runtime stage from chainguard
#FROM cgr.dev/chainguard/jre:openjdk-25

WORKDIR /app

# Create non-root user
RUN useradd --system --create-home spring

# Copy the generated Spring Boot JAR
COPY --from=build /app/user-service/target/*.jar app.jar

#from chainguard
#COPY --from=build --chown=65532:65532 \
#    /app/user-service/target/*.jar /app/app.jar

# Run application as non-root user
USER spring

# Start Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
