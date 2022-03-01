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
        DOCKER_IMAGE_NAME_OLD = ''
        DOCKER_CONTAINER_NAME = ''
    }

    stages {
        /*
        stage('get_commit_details') {
                steps {
                    script {
                        env.GIT_COMMIT_MSG = sh (script: 'git log -1 --pretty=%B ${GIT_COMMIT}', returnStdout: true).trim()
                        env.GIT_AUTHOR = sh (script: 'git log -1 --pretty=%cn ${GIT_COMMIT}', returnStdout: true).trim()
                    }
                }
            }
        */
        stage("Maven Build") {
            steps {
                script {
                    sh "mvn clean package -DskipTests=true -Drevision=${currentBuild.number}"
                }
            }
        }
        stage("Publish to Nexus Repository Manager") {
            steps {
                /*
                timeout(time: 5, unit: 'MINUTES'){
                    input message: "Should we deploy this artifact on Nexus?", ok: "Yes, we should."
                }
                */
                script {
                    pom = readMavenPom file: "pom.xml";
                    def nexusRepoName = pom.version.endsWith("SNAPSHOT") ? NEXUS_REPOSITORY_SNAPSHOT : NEXUS_REPOSITORY_RELEASE
                    echo 'nexusRepoName:------> ' +nexusRepoName
                    // Find built artifact under target folder
                    filesByGlob = findFiles(glob: "target/*.${pom.packaging}");
                    echo 'filesByGlob' +filesByGlob
                    // Print some info from the artifact found
                    echo "${filesByGlob[0].name} ${filesByGlob[0].path} ${filesByGlob[0].directory} ${filesByGlob[0].length} ${filesByGlob[0].lastModified}"
                    // Extract the path from the File found
                    artifactPath = filesByGlob[0].path;
                    echo 'artifactPath: '+ artifactPath
                    // Assign to a boolean response verifying If the artifact name exists
                    artifactExists = fileExists artifactPath;
                    echo 'artifactExists:' +artifactExists
                    if(artifactExists) {
                        echo "*** File: ${artifactPath}, group: ${pom.groupId}, packaging: ${pom.packaging}, version ${pom.version}";
                        //artifactPath: target/spring-boot-graphql-98-RELEASE.jar
                        def values = artifactPath.split('target/'+pom.name+'-');
                        DOCKER_IMAGE = REGISTRY +':'+  currentBuild.number
                        int buildNumber = currentBuild.number;
                        int a = 1;
                        int previousTag = buildNumber - a;
                        DOCKER_IMAGE_NAME_OLD = REGISTRY +':'+ previousTag
                        DOCKER_CONTAINER_NAME = pom.name
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
                /*
                timeout(time: 5, unit: 'MINUTES'){
                        input message: "Should we build the docker image?", ok: "Yes, we should."
                }
                */
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
        stage('Deploy') {
            steps {
                timeout(time: 5, unit: 'MINUTES'){
                        input message: "Should we run the docker image?", ok: "Yes, we should."
                }
                script{
                    def doc_containers = sh(returnStdout: true, script: 'docker ps --format "{{.ID}}||{{.Image}}||{{.Names}}"')
                    def finalVersion = doc_containers.split('\n');
                    def containerId='';
                    for (i in finalVersion) {
                        if(i.contains(REGISTRY)){
                            echo 'ENCONTREI O CONTAINER QUE BUSCO: ' +i
                            DOCKER_CONTAINER_ID = i.substring(0,12)
                            echo 'Container id= ' + DOCKER_CONTAINER_ID
                            sh "docker stop ${DOCKER_CONTAINER_ID} | true"
                            sh "docker container rm ${DOCKER_CONTAINER_ID} | true"
                        }
                    }
                }
                sh "docker run --name ${DOCKER_CONTAINER_NAME} -d -p 8091:8080 ${DOCKER_IMAGE}"
            }
        }
    }
    post {
        always {
            sh "docker image prune -a -f"
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
