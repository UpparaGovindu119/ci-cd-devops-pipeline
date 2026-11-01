
pipeline {
    agent any

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
        PATH = "/usr/lib/jvm/java-17-openjdk-amd64/bin:${env.PATH}"
        IMAGE_NAME = 'upparagovindu/ci-cd-devops-pipeline'
        SONAR_HOST_URL = 'http://localhost:9000'
    }

    stages {

        stage('1. Checkout Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/UpparaGovindu119/ci-cd-devops-pipeline.git'
            }
        }

        stage('2. Build & Unit Tests') {
            steps {
                sh '''
                    java -version
                    mvn -version
                    mvn clean package
                '''
            }
        }

        stage('3. SonarQube Analysis') {
            steps {
                withCredentials([
                    string(
                        credentialsId: 'sonar-token-id',
                        variable: 'SONAR_TOKEN'
                    )
                ]) {
                    sh '''
                        mvn sonar:sonar \
                        -Dsonar.projectKey=ci-cd-devops-pipeline \
                        -Dsonar.host.url="$SONAR_HOST_URL" \
                        -Dsonar.token="$SONAR_TOKEN"
                    '''
                }
            }
        }

        stage('4. Trivy Security Scan') {
            steps {
                sh '''
                    trivy fs \
                    --severity HIGH,CRITICAL \
                    --exit-code 1 .
                '''
            }
        }

        stage('5. Build & Push Docker Image') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-secret-id',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )
                ]) {
                    sh '''
                        echo "$DOCKER_PASSWORD" | docker login \
                        -u "$DOCKER_USER" --password-stdin

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

        stage('6. Deploy via Ansible') {
            steps {
                sh 'ansible-playbook -i localhost, deploy.yml'
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo 'Jenkins Pipeline Executed Successfully!'
        }
        failure {
            echo 'Jenkins Pipeline Failed! Check Console Output.'
        }
    }
}
