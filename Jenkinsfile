pipeline {
    agent any

    environment {
        APP_NAME = 'java-cicd-pipeline-app'
        DOCKER_IMAGE = "mumtaz2029/${APP_NAME}:${BUILD_NUMBER}"
    }

    tools {
        jdk 'jdk21'
        maven 'maven3'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat 'mvn -B clean package'
            }
        }

        stage('Test') {
            steps {
                bat 'mvn test'
            }
        }

        stage('Build Docker Image') {
            steps {
                bat "docker build -t %DOCKER_IMAGE% ."
            }
        }

        stage('Deploy Container') {
            steps {
                bat 'docker rm -f java-cicd-pipeline-app || exit 0'
                bat "docker run -d -p 8080:8080 --name java-cicd-pipeline-app %DOCKER_IMAGE%"
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully.'
        }
        failure {
            echo 'Pipeline failed. Check the stage logs for details.'
        }
    }
}
