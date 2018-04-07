stage('Build Test Container') {
    node {
        deleteDir()
        checkout scm
        docker.build('jenny_test_container')
    }
}

stage('Run Tests') {
    node {
        docker.image('jenny_test_container')
              .inside("-v /var/run/docker.sock:/var/run/docker.sock:rw") {
            sh('mkdir -p /tmp/test_jenny')
            dir('/tmp/test_jenny')
            checkout scm
            sh """
                bin/test-jenny.sh --debug
            """
        }
    }
}
