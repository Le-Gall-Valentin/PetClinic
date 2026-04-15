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

        stage('Upload JARs To Nexus') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexus-jenkins', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                    sh '''
                        set -eux

                        BUILD_TAG_SAFE="$(echo "${BUILD_TAG}" | tr ' /:' '---')"

                        find . -path '*/target/*.jar' ! -name '*.jar.original' | while read -r jar; do
                          base="$(basename "$jar")"
                          module="$(basename "$(dirname "$(dirname "$jar")")")"

                          curl -f -u "$NEXUS_USER:$NEXUS_PASS" \
                            --upload-file "$jar" \
                            "http://127.0.0.1:8081/repository/jenkins-jars/${JOB_NAME}/${BUILD_TAG_SAFE}/${module}/${base}"
                        done
                    '''
                }
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
