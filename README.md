# CI/CD Pipeline Project

This project automates the CI/CD pipeline and deployment for a Java Spring Boot application using GitHub Actions, Jenkins, Docker, SonarQube, Trivy, Ansible, and AWS.

## Tech Stack
- Git & GitHub
- GitHub Actions
- Jenkins
- Maven
- SonarQube
- Trivy
- Docker & Docker Hub
- Ansible
- AWS (EC2)

## Pipeline and Deployment Stages
1. Checkout: Pulls the code from the GitHub repository.
2. Build & Test: Compiles the Java application and runs tests using Maven.
3. Code Quality Scan: Checks the code quality and bugs using SonarQube.
4. Security Scan: Scans the workspace for security vulnerabilities using Trivy.
5. Docker Build & Push: Builds the Docker image and pushes it to Docker Hub.
6. Deployment: 
   - Deploys to AWS EC2 directly using GitHub Actions (configured with EC2 IP, .pem key, and username).
   - Also automates deployment to AWS using Ansible integrated within the Jenkins pipeline.

## Author
Govindu Uppara
