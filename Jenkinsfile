
pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
        disableConcurrentBuilds()
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

                sh '''
                    echo "Checkout completed"
                    echo "Commit: $GIT_COMMIT"
                '''
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
                        command -v "$tool" || {
                            echo "ERROR: $tool is not installed"
                            exit 1
                        }
                    done

                    echo "Checking Docker access..."
                    docker info

                    echo "Checking required project files..."
                    test -f pom.xml
                    test -f Dockerfile
                    test -f deploy.yml

                    echo "All preflight checks passed"
                '''
            }
        }

        stage('3. Build & Unit Tests') {
            steps {
                sh '''
                    set -e
                    mvn clean package
                '''
            }
        }

        stage('4. SonarQube Analysis') {
            steps {
                withCredentials([
                    string(
                        credentialsId: 'sonar-token-id',
                        variable: 'SONAR_TOKEN'
                    )
                ]) {
                    sh '''
                        set -e

                        echo "Checking SonarQube connectivity..."
                        curl --fail --silent \
                            "$SONAR_HOST_URL/api/system/status"

                        echo "Running SonarQube analysis..."

                        mvn sonar:sonar \
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

                    trivy fs \
                        --severity HIGH,CRITICAL \
                        --exit-code 1 \
                        .
                '''
            }
        }

        stage('6. Build & Push Docker Image') {
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
                            -t "$IMAGE_NAME:$GIT_COMMIT" .

                        docker push "$IMAGE_NAME:latest"
                        docker push "$IMAGE_NAME:$GIT_COMMIT"

                        docker logout
                    '''
                }
            }
        }

        stage('7. Deploy via Ansible') {
            steps {
                sh '''
                    set -e

                    ansible-playbook \
                        -i localhost, \
                        deploy.yml
                '''
            }
        }
    }

    post {
        success {
            echo 'SUCCESS: CI/CD Pipeline completed!'
        }

        failure {
            echo 'FAILURE: Check the failed stage in Console Output.'
        }

        always {
            echo 'Jenkins Pipeline execution finished.'
        }
    }
}
