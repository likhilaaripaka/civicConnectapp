# Multi-stage build for optimized production image
FROM maven:3.9.4-openjdk-17-slim AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Production stage
FROM openjdk:17-jdk-slim

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Create uploads directory with proper permissions
RUN mkdir -p uploads && chown -R appuser:appuser uploads

# Copy built jar from build stage
COPY --from=build /app/target/civicconnect-0.0.1-SNAPSHOT.jar app.jar

# Change ownership of the app
RUN chown appuser:appuser app.jar

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/ || exit 1

# Run the application with optimized JVM settings
CMD ["java", "-Xmx512m", "-Xms256m", "-XX:+UseG1GC", "-XX:+UseStringDeduplication", "-jar", "app.jar"]
