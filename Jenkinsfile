pipeline {
    agent any

    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '20', artifactNumToKeepStr: '20'))
    }

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-21-openjdk-amd64'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
        JENKINS_TMP = '/var/snap/jenkins/common/petclinic-tmp'
        JENKINS_LOGS = '/var/snap/jenkins/common/petclinic-logs'
        JAVA_TOOL_OPTIONS = "-Djava.io.tmpdir=/var/snap/jenkins/common/petclinic-tmp -DLOG_PATH=/var/snap/jenkins/common/petclinic-logs"
    }

    stages {
        stage('Build And Test') {
            steps {
                sh '''
                    set -eux
                    mkdir -p "$JENKINS_TMP" "$JENKINS_LOGS"
                    chmod +x mvnw
                    ./mvnw -B -ntp -PbuildVue clean verify
                '''
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('Local SonarQube') {
                    sh '''
                        set -eux
                        ./mvnw -B -ntp sonar:sonar \
                          -Dsonar.projectKey=petclinic \
                          -Dsonar.projectName=PetClinic
                    '''
                }
            }
        }

        stage('Validate Outputs') {
            steps {
                sh '''
                    set -eux
                    test -f spring-petclinic-api-gateway/target/classes/static/index.html
                    find . -path '*/target/*.jar' ! -name '*.jar.original' | sort
                    find . -path '*/target/site/jacoco/index.html' | sort
                    find . -path '*/target/surefire-reports/*.xml' | sort || true
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
