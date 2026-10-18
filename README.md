# Enterprise Java CI/CD DevOps Pipeline

Production-grade CI/CD pipeline implementation for a Spring Boot Java microservice, demonstrating end-to-end DevOps lifecycle practices.

## Architecture & Tech Stack
- **Application:** Java 17, Spring Boot, Maven
- **Containerization:** Docker (Multi-stage builds, non-root `appuser` security)
- **CI/CD Automation:** Jenkins / GitHub Actions
- **Quality & Security:** SonarQube, Trivy image & filesystem scanning

## Key Engineering Highlights
- **Optimized Docker Image:** Multi-stage build separates the Maven build environment from the lightweight `eclipse-temurin:17-jre-slim` runtime environment, minimizing image size and security vulnerabilities.
- **Security Best Practices:** Containers execute under a non-root user (`appuser`) to adhere to CIS benchmarks.
- **Automated Pipeline:** Integrated code build, unit tests, static code analysis, vulnerability scanning, and container registry publishing.
