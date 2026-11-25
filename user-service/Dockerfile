
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

WORKDIR /app

# Copy the generated Spring Boot JAR
COPY --from=build /app/user-service/target/*.jar app.jar

# Application port
EXPOSE 8080

# Start Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
