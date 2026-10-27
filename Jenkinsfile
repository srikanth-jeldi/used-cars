pipeline {
    agent any

    tools {
        maven 'maven-3'
    }

    environment {
        // Gateway image ki oka repo peru
        DOCKER_REPO = "srikanthjeldi/used-cars-gateway"
    }

    stages {

        stage('Build JAR') {
            steps {
                // whole multi-module project build
                sh 'mvn -B -ntp clean verify'
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    sh """
                        # gateway-service folder lo unna Dockerfile use avvali
                        docker build -t ${DOCKER_REPO}:latest ./gateway-service
                    """
                }
            }
        }

        stage('Docker Login') {
            steps {
                withCredentials([usernamePassword(
                        credentialsId: 'docker-hub-creds',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh "echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin"
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                sh "docker push ${DOCKER_REPO}:latest"
            }
        }
    }

    post {
        success {
            echo '🎉 Build + Docker Push SUCCESS!'
        }
        failure {
            echo '❌ Build FAILED'
        }
    }
}
