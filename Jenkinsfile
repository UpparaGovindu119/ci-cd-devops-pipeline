
pipeline {
    agent any

    environment {
        // Jenkins service uses Java 21.
        // Maven build uses Java 17.
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
        PATH+JAVA17 = '/usr/lib/jvm/java-17-openjdk-amd64/bin'

        SONAR_TOKEN = credentials('sonar-token-id')
        IMAGE_NAME = 'upparagovindu/ci-cd-devops-pipeline'
    }

    stages {

        // 1. Checkout source code from GitHub
        stage('1. Checkout Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/UpparaGovindu119/ci-cd-devops-pipeline.git'
            }
        }

        // 2. Verify Java and Maven versions
        stage('2. Java & Maven Verification') {
            steps {
                sh '''
                    echo "===== JAVA VERSION ====="
                    java -version

                    echo "===== JAVA HOME ====="
                    echo "$JAVA_HOME"

                    echo "===== MAVEN VERSION ====="
                    mvn -version
                '''
            }
        }

        // 3. Build application and run unit tests
        stage('3. Build & Unit Tests') {
            steps {
                sh 'mvn clean package'
            }
        }

        // 4. SonarQube code quality analysis
        stage('4. SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQubeServer') {
                    sh '''
                        mvn sonar:sonar \
                        -Dsonar.projectKey=ci-cd-devops-pipeline \
                        -Dsonar.token="$SONAR_TOKEN"
                    '''
                }
            }
        }

        // 5. Trivy filesystem security scan
        stage('5. Trivy Security Scan') {
            steps {
                sh 'trivy fs --severity HIGH,CRITICAL .'
            }
        }

        // 6. Build Docker image and push to DockerHub
        stage('6. Build & Push Docker Image') {
            steps {
                script {
                    docker.withRegistry(
                        'https://index.docker.io/v1/',
                        'dockerhub-secret-id'
                    ) {

                        // Build image only once
                        def customImage =
                            docker.build("${IMAGE_NAME}:latest")

                        // Push latest tag
                        customImage.push('latest')

                        // Push full Git commit SHA tag
                        customImage.push(env.GIT_COMMIT)
                    }
                }
            }
        }

        // 7. Deploy application using Ansible
        stage('7. Deploy via Ansible') {
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
            echo 'SUCCESS: Jenkins Pipeline Executed Successfully!'
            echo 'Application build, security scan, Docker push and deployment completed.'
        }

        failure {
            echo 'FAILURE: Jenkins Pipeline Failed!'
            echo 'Check the Console Output to identify the failed stage.'
        }
    }
}
