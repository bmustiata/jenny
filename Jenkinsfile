properties([
    parameters([
        string(name: 'JENNY_DOCKER_RUN_ARGS', defaultValue: '-v /etc/passwd:/etc/passwd:ro -v /etc/group:/etc/group:ro',
                description: 'Extra docker run arguments.')
    ])
])

stage('Build Test Container') {
    node {
        deleteDir()
        checkout scm

        docker.build('jenny_test_container', "--build-arg JENNY_WORKSPACE_FOLDER=${env.PWD} .")
              .inside("-v /var/run/docker.sock:/var/run/docker.sock:rw ${JENNY_DOCKER_RUN_ARGS}") {
                    checkout scm
                    sh "bin/test-jenny.sh"
        }
    }
}

