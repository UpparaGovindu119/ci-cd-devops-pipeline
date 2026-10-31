pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-secret-id')
        SONAR_TOKEN = credentials('sonar-token-id')
        IMAGE_NAME = 'upparagovindu/ci-cd-devops-pipeline'
    }

    stages {
        stage('1. Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/UpparaGovindu119/ci-cd-devops-pipeline.git'
            }
        }

        stage('2. Build & Unit Tests') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('3. SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQubeServer') {
                    sh "mvn sonar:sonar -Dsonar.projectKey=ci-cd-devops-pipeline -Dsonar.login=${SONAR_TOKEN}"
                }
            }
        }

        stage('4. Trivy Security Scan') {
            steps {
                sh 'trivy fs --severity HIGH,CRITICAL .'
            }
        }

        stage('5. Build & Push Docker Image') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'dockerhub-secret-id') {
                        def customImage = docker.image("${IMAGE_NAME}:latest")
                        customImage.build()
                        customImage.push()
                        
                        // Push with git commit sha tag as well
                        def shaImage = docker.image("${IMAGE_NAME}:${env.BUILD_ID}")
                        shaImage.build()
                        shaImage.push()
                    }
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
            echo '🎉 Jenkins Pipeline Executed Successfully! App deployed with MongoDB.'
        }
        failure {
            echo '❌ Jenkins Pipeline Failed! Check console logs.'
        }
    }
}
