stage('Build Test Container') {
    node {
        deleteDir()
        checkout scm

        docker.build('jenny_test_container')
              .inside("-v /var/run/docker.sock:/var/run/docker.sock:rw") {
                    checkout scm
                    sh "bin/test_jenny.py"
                    cat "/tmp/jenny_*"
        }
    }
}

