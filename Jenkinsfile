stage('Build Test Container') {
    node {
        deleteDir()

        checkout scm

        dockerBuild file: './Dockerfile',
            tags: ['jenny_test_container']
    }
}

stage('Run Tests') {
    node {
        dockerRun image: 'jenny_test_container',
            volumes: [
                '/var/run/docker.sock:/var/run/docker.sock:rw'
            ],
            remove: true
    }
}
