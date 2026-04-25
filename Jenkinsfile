pipeline {
    agent any

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
                sh 'mvn -B clean compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn -B test'
            }
        }

        stage('Verify and Package') {
            steps {
                sh 'mvn -B verify'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonar-server') {
                    sh 'mvn -B sonar:sonar'
                }
            }
        }
    }

    post {
        success {
            echo 'Build, packaging, and SonarQube analysis completed successfully.'
        }
        failure {
            echo 'Pipeline failed. Check Jenkins console output and SonarQube logs.'
        }
    }
}
