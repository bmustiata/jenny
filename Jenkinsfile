stage('Build Test Container') {
    node {
        dockerBuild file: './Dockerfile',
            tags: ['jenny_test_container']
    }
}

stage('Run Tests') {
    node {
        dockerRun image: 'jenny_test_container',
            remove: true
    }
}
