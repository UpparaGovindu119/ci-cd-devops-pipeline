pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-secret-id')
        IMAGE_NAME = 'upparagovindu/ci-cd-devops-pipeline'
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/UpparaGovindu119/ci-cd-devops-pipeline.git'
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    def scannerHome = tool 'SonarQubeScanner'
                    withSonarQubeEnv('SonarQubeServer') {
                        sh "${scannerHome}/bin/sonar-scanner"
                    }
                }
            }
        }

        stage('Trivy Security Scan') {
            steps {
                sh 'trivy fs --severity HIGH,CRITICAL .'
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'dockerhub-secret-id') {
                        def customImage = docker.image("${IMAGE_NAME}:latest")
                        customImage.build()
                        customImage.push()
                    }
                }
            }
        }

        stage('Deploy via Ansible') {
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
            echo 'Pipeline Executed Successfully! Application deployed.'
        }
        failure {
            echo 'Pipeline Failed! Check logs.'
        }
    }
}
