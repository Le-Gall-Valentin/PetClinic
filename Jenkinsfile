pipeline {
    agent any

    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '20', artifactNumToKeepStr: '20'))
    }

    stages {
        stage('Tooling') {
            steps {
                sh 'chmod +x mvnw'
                sh 'java -version'
                sh 'npm -v'
                sh './mvnw -v'
            }
        }

        stage('Build And Test') {
            steps {
                sh './mvnw -B -ntp -PbuildVue clean verify'
            }
        }

        stage('Validate Outputs') {
            steps {
                sh "test -f spring-petclinic-api-gateway/target/classes/static/index.html"
                sh "find . -path '*/target/*.jar' ! -name '*.jar.original' | sort"
                sh "find . -path '*/target/site/jacoco/index.html' | sort"
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

            archiveArtifacts(
                artifacts: '**/target/*.jar,spring-petclinic-api-gateway/target/classes/static/**/*,**/target/site/jacoco/**/*,**/target/surefire-reports/**/*',
                excludes: '**/*.jar.original',
                allowEmptyArchive: true,
                fingerprint: true
            )
        }
    }
}
