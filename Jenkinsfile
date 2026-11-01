
pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
        disableConcurrentBuilds()
        timestamps()
    }

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
        PATH = "/usr/lib/jvm/java-17-openjdk-amd64/bin:${env.PATH}"

        IMAGE_NAME = 'upparagovindu/ci-cd-devops-pipeline'
        SONAR_HOST_URL = 'http://localhost:9000'
        SONAR_PROJECT_KEY = 'ci-cd-devops-pipeline'
    }

    stages {

        stage('1. Checkout Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/UpparaGovindu119/ci-cd-devops-pipeline.git'

                script {
                    env.COMMIT_SHA = sh(
                        script: 'git rev-parse HEAD',
                        returnStdout: true
                    ).trim()

                    echo "Checked out commit: ${env.COMMIT_SHA}"
                }
            }
        }

        stage('2. Preflight Checks') {
            steps {
                sh '''
                    set -e

                    echo "Checking Java..."
                    java -version

                    echo "Checking Maven..."
                    mvn -version

                    echo "Checking required tools..."

                    for tool in docker trivy ansible curl; do
                        if ! command -v "$tool" >/dev/null 2>&1; then
                            echo "ERROR: $tool is not installed"
                            exit 1
                        fi

                        echo "$tool is available"
                    done

                    echo "Checking Docker daemon..."
                    docker info >/dev/null

                    echo "Checking project files..."
                    test -f pom.xml
                    test -f Dockerfile
                    test -f deploy.yml

                    echo "Preflight checks passed"
                '''
            }
        }

        stage('3. Build & Unit Tests') {
            steps {
                sh '''
                    set -e

                    mvn -B clean verify
                '''
            }
        }

        stage('4. SonarQube Analysis') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'sonar-token-id',
                        usernameVariable: 'SONAR_USERNAME',
                        passwordVariable: 'SONAR_TOKEN'
                    )
                ]) {
                    sh '''
                        set -e

                        echo "Checking SonarQube..."

                        curl --fail --silent \
                            "$SONAR_HOST_URL/api/system/status"

                        echo "Starting SonarQube analysis..."

                        mvn -B \
                            org.sonarsource.scanner.maven:sonar-maven-plugin:5.3.0.6276:sonar \
                            -Dsonar.projectKey="$SONAR_PROJECT_KEY" \
                            -Dsonar.host.url="$SONAR_HOST_URL" \
                            -Dsonar.token="$SONAR_TOKEN"
                    '''
                }
            }
        }

        stage('5. Trivy Security Scan') {
            steps {
                sh '''
                    set -e

                    echo "Running Trivy filesystem scan..."

                    trivy fs \
                        --severity HIGH,CRITICAL \
                        --format table \
                        --exit-code 0 \
                        .
                '''
            }
        }

        stage('6. Docker Build & Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-secret-id',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )
                ]) {
                    sh '''
                        set -e

                        echo "$DOCKER_PASSWORD" | docker login \
                            --username "$DOCKER_USER" \
                            --password-stdin

                        docker build \
                            -t "$IMAGE_NAME:latest" \
                            -t "$IMAGE_NAME:$COMMIT_SHA" .

                        docker push "$IMAGE_NAME:latest"

                        docker push "$IMAGE_NAME:$COMMIT_SHA"

                        docker logout
                    '''
                }
            }
        }

        stage('7. Deploy via Ansible') {
            steps {
                sh '''
                    set -e

                    echo "Starting Ansible deployment..."

                    ansible-playbook \
                        -i localhost, \
                        -c local \
                        deploy.yml

                    echo "Ansible deployment completed"
                '''
            }
        }
    }

    post {

        success {
            echo 'SUCCESS: CI/CD Pipeline completed!'
            echo 'Docker image pushed successfully.'
            echo 'Ansible deployment completed.'
        }

        failure {
            echo 'FAILURE: Check the first failed stage in Console Output.'
        }

        always {
            echo 'Pipeline execution finished.'
        }
    }
}
