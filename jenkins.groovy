pipeline {
    agent any
	 tools {
        // Install the Maven version configured as "M3" and add it to the path.
        maven "MAVEN_HOME"
    }
    stages {
        stage('Clone') {
            steps {
                // Get some code from a GitHub repository
               
               git branch: '${git.branch}', credentialsId: '7195b9c2-b683-4ac7-a7ef-53d85bf7e285', url: '${git.url}'

            }
        }
		stage('Build') {
            steps {
                 bat "mvn -Dmaven.test.failure.ignore=true clean package"
            }
        }
        	stage('Approval to Deploy') {
            steps {
             script{
                  timeout(time: 10, unit: 'MINUTES') {
                 
                    input(id: "Deploy Gate", message: "Deploy ${params.appName}?", ok: 'Deploy')
					}
					}
					
            }
        }
		stage('Deploy') {
		environment {
		 ANYPOINT_CREDENTIALS = credentials('ANYPOINT_CREDENTIALS')
		}
        steps {
                 bat """mvn deploy -Dmule.username=${ANYPOINT_CREDENTIALS_USR} -Dmule.password=${ANYPOINT_CREDENTIALS_PSW} -Dmule.environment=${params.env} -Dmule.businessGroup=${params.BG} -Dmule.env=dev -Dmule.version=${params.mule_version} -Dmule.workers=${params.workers}  -Dmule.skip=false -Dmule.applicationName=${params.appName} -Dmule.workerType=${params.workerType}"""
            }
        }
    }
}