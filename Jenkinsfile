stage('Build Test Container') {
    node {
        deleteDir()
        checkout scm

        sh 'set'

        docker.build('jenny_test_container', "--build-arg JENNY_WORKSPACE_FOLDER=${pwd()} .")
              .inside("-v /var/run/docker.sock:/var/run/docker.sock:rw") {
                    checkout scm
                    sh "bin/test-jenny.sh"
        }
    }
}

