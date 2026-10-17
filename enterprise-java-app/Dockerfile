# Stage 1: Build the application with Maven
FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application on a slim, secure JRE image
FROM openjdk:17-jdk-slim
WORKDIR /app

# Create a non-root system user for security best practices (Real-time standard)
RUN groupadd -r devops && useradd -r -g devops appuser
COPY --from=builder /build/target/enterprise-java-app-1.0.0.jar app.jar
RUN chown -R appuser:devops /app

USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
