pipeline {
    agent any

    tools {
        maven 'maven-3'
    }

    stages {
        stage('Build') {
            steps {
                sh 'mvn -version'
                sh 'mvn -B clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Archive JARs') {
            steps {
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            echo '✅ used-cars build SUCCESS bro!'
        }
        failure {
            echo '❌ Build FAILED, console output chusko.'
        }
    }
}
