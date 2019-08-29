node {
    agent any

    stages {
        stage ('Checking git'){
            steps{
                scmSkip(deleteBuild: true)
            }
        }

        stage ("Building apk"){
            steps{
                sh './gradlew app:assembleRelease'
            }
        }

        stage ("Signing apk"){
            steps{
                sh 'jarsigner -verbose -keystore ~/signing/jenkins.jks -storepass ${SIGNPASS} -signedjar app/release/HTTPServiceAttacker.apk app/build/outputs/apk/release/app-release-unsigned.apk key0'
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'app/release/*.apk', fingerprint: true
        }
    }
}