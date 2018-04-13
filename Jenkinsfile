stage('Build Test Container') {
    node {
        deleteDir()
        checkout scm

        docker.build('jenny_test_container')
              .inside("-v /var/run/docker.sock:/var/run/docker.sock:rw") {
                    checkout scm
                    try {
                        sh "bin/test_jenny.py"
                    } finally {
                        sh "cat /tmp/jenny_*"
                    }
        }
    }
}

