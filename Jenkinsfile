
pipeline {
    agent any

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
        PATH = "/usr/lib/jvm/java-17-openjdk-amd64/bin:${env.PATH}"

        SONAR_TOKEN = credentials('sonar-token-id')
        IMAGE_NAME = 'upparagovindu/ci-cd-devops-pipeline'
    }

    stages {
        stage('1. Checkout Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/UpparaGovindu119/ci-cd-devops-pipeline.git'
            }
        }

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

        stage('3. Build & Unit Tests') {
            steps {
                sh 'mvn clean package'
            }
        }

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

        stage('5. Trivy Security Scan') {
            steps {
                sh 'trivy fs --severity HIGH,CRITICAL .'
            }
        }

        stage('6. Build & Push Docker Image') {
            steps {
                script {
                    docker.withRegistry(
                        'https://index.docker.io/v1/',
                        'dockerhub-secret-id'
                    ) {
                        def customImage =
                            docker.build("${IMAGE_NAME}:latest")

                        customImage.push('latest')
                        customImage.push(env.GIT_COMMIT)
                    }
                }
            }
        }

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
            echo 'Jenkins Pipeline Executed Successfully!'
        }

        failure {
            echo 'Jenkins Pipeline Failed! Check Console Output.'
        }
    }
}
