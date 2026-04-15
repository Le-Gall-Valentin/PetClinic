pipeline {
    agent any

    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '20', artifactNumToKeepStr: '20'))
    }

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-21-openjdk-amd64'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
        JAVA_TOOL_OPTIONS = "-Djava.io.tmpdir=${env.WORKSPACE}/.tmp -DLOG_PATH=${env.WORKSPACE}/logs"
    }

    stages {
        stage('Build And Test') {
            steps {
                sh '''
                    set -eux
                    mkdir -p "$WORKSPACE/.tmp" "$WORKSPACE/logs"
                    chmod +x mvnw
                    ./mvnw -B -ntp -PbuildVue clean verify
                '''
            }
        }

        stage('Validate Outputs') {
            steps {
                sh '''
                    set -eux
                    test -f spring-petclinic-api-gateway/target/classes/static/index.html
                    find . -path '*/target/*.jar' ! -name '*.jar.original' | sort
                    find . -path '*/target/site/jacoco/index.html' | sort
                '''
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
