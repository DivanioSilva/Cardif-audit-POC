import hudson.model.*
pipeline {
    agent any
    tools {
        maven "Maven-3.6.3"
    }
    environment {
        NEXUS_VERSION = "nexus3"
        NEXUS_PROTOCOL = "http"
        NEXUS_URL = "192.168.1.149:8081"
        NEXUS_REPOSITORY_RELEASE = "maven-nexus-repo-release/"
        NEXUS_REPOSITORY_SNAPSHOT = "maven-nexus-repo-snapshot/"
        NEXUS_CREDENTIAL_ID = "nexus3"
        REPOSITORY = "https://github.com/DivanioSilva/Cardif-audit-POC.git"
        REGISTRY = "dcsilva/audit-pic"
        REGISTRY_CREDENTIAL = "DockerHub"
        DOCKER_IMAGE = ''
        DOCKER_CONTAINER_NAME = ''
        DOCKER_IMAGE_OLD = ''
    }

    stages {
        stage('get_commit_details') {
                steps {
                    script {
                        env.GIT_COMMIT_MSG = sh (script: 'git log -1 --pretty=%B ${GIT_COMMIT}', returnStdout: true).trim()
                        env.GIT_AUTHOR = sh (script: 'git log -1 --pretty=%cn ${GIT_COMMIT}', returnStdout: true).trim()
                    }
                }
            }
        stage("Maven Build") {
            steps {
                script {
                    sh "mvn clean package -DskipTests=true -Drevision=${currentBuild.number}"
                }
            }
        }
        stage("Publish to Nexus Repository Manager") {
            steps {
                timeout(time: 5, unit: 'MINUTES'){
                    input message: "Should we deploy this artifact on Nexus?", ok: "Yes, we should."
                }
                script {
                    pom = readMavenPom file: "pom.xml";
                    def nexusRepoName = pom.version.endsWith("SNAPSHOT") ? NEXUS_REPOSITORY_SNAPSHOT : NEXUS_REPOSITORY_RELEASE
                    echo 'nexusRepoName:------> ' +nexusRepoName
                    filesByGlob = findFiles(glob: "target/*.${pom.packaging}");
                    echo 'filesByGlob' +filesByGlob
                    echo "${filesByGlob[0].name} ${filesByGlob[0].path} ${filesByGlob[0].directory} ${filesByGlob[0].length} ${filesByGlob[0].lastModified}"
                    artifactPath = filesByGlob[0].path;
                    echo 'artifactPath: '+ artifactPath
                    artifactExists = fileExists artifactPath;
                    echo 'artifactExists:' +artifactExists
                    if(artifactExists) {
                        echo "*** File: ${artifactPath}, group: ${pom.groupId}, packaging: ${pom.packaging}, version ${pom.version}";
                        def values = artifactPath.split('target/'+pom.name+'-');
                        DOCKER_IMAGE = REGISTRY +':'+  currentBuild.number
                        DOCKER_CONTAINER_NAME = pom.name
                        int buildNumber = currentBuild.number;
                        int a = 1;
                        int previousTag = buildNumber - a;
                        DOCKER_IMAGE_OLD = REGISTRY +':'+ previousTag
                        echo 'result: '+result
                        echo 'values: '+values
                        def finalVersion = values[1].split('.'+pom.packaging);
                        echo 'finalVersion: '+finalVersion
                        nexusArtifactUploader artifacts: [
                            [
                                artifactId: pom.name,
                                classifier: '',
                                file: artifactPath,
                                type: pom.packaging]
                            ],
                            credentialsId: NEXUS_CREDENTIAL_ID,
                            groupId: pom.groupId,
                            nexusUrl: NEXUS_URL,
                            nexusVersion: NEXUS_VERSION,
                            protocol: NEXUS_PROTOCOL,
                            repository: nexusRepoName,
                            version: finalVersion[0]
                    } else {
                        error "*** File: ${artifactPath}, could not be found";
                    }
                }
            }
        }
        stage('Building the Docker image') {
            steps {
                timeout(time: 5, unit: 'MINUTES'){
                        input message: "Should we build the docker image?", ok: "Yes, we should."
                }
                script {
                    dockerImage = docker.build(REGISTRY + ":$BUILD_NUMBER")
                }
            }
        }
        stage('Pushing Docker image to DockerHub') {
            steps {
                script {
                    docker.withRegistry( '', REGISTRY_CREDENTIAL ) {
                        dockerImage.push()
                    }
                }
            }
        }
        stage('Run Docker image') {
            steps {
                timeout(time: 5, unit: 'MINUTES'){
                        input message: "Should we run the docker image?", ok: "Yes, we should."
                }
            }
        }
        stage('Deploy') {
            steps {
                echo 'Previous docker image: ---> ' + DOCKER_IMAGE_OLD
                echo 'Docker image: ---> ' + DOCKER_IMAGE
                echo 'Docker container name: ---> ' +DOCKER_CONTAINER_NAME
                sh "docker stop ${DOCKER_IMAGE_OLD} | true"
                sh "docker rm ${DOCKER_IMAGE_OLD} | true"
                sh "docker run --name ${DOCKER_CONTAINER_NAME} -d -p 8091:8080 ${DOCKER_IMAGE}"

            }
        }
        stage('Cleaning up') {
            steps {
                sh "docker rmi $registry:$BUILD_NUMBER"
            }
        }
    }
    post {
        always {
                deleteDir()
            }
        success {
            echo "Build Success"
            echo "Successfully built ${env.JOB_BASE_NAME} - ${env.BUILD_ID} on ${env.BUILD_URL}"
        }
        failure {
            echo "Build Failed - ${env.JOB_BASE_NAME} - ${env.BUILD_ID} on ${env.BUILD_URL}"
        }
        aborted {
            echo " ${env.JOB_BASE_NAME} Build - ${env.BUILD_ID} Aborted!"
        }
    }
}
