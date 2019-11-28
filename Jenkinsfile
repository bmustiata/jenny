properties([
    parameters([
        string(name: 'JENNY_DOCKER_UID', defaultValue: '1000:1000',
                description: 'Should correspond to the uid:gid of the user running docker.'),
        string(name: 'JENNY_DOCKER_GID', defaultValue: '999',
                description: 'Should be the group ID of the docker installation.')
    ])
])

JENNY_DOCKER_UID = params.JENNY_DOCKER_UID ?: '1000:1000'
JENNY_DOCKER_GID = params.JENNY_DOCKER_GID ?: '999'

stage('Build Test Container') {
    node {
        deleteDir()
        checkout scm

        docker.build('jenny_test_junit',
                     'features/junit-support')

        // jenny needs access to the workFolder to mount it inside the containers,
        // and the tests output is checked against /tmp
        def jennyParentIsInDocker = sh([
            script: "cat /proc/1/cgroup | grep docker",
            returnStatus: true
        ]) == 0

        def jennyParentIsInKubernetes = sh([
            script: "cat /proc/1/cgroup | grep kubepods",
            returnStatus: true
        ]) == 0

        sh """
            cat /proc/1/cgroup
        """

        echo "in docker: $jennyParentIsInDocker"
        echo "in kubernetes: $jennyParentIsInKubernetes"

        def extraMount = (jennyParentIsInDocker || jennyParentIsInKubernetes) ? " -v /tmp:/tmp" : ""

        docker.build('jenny_test_container')
              .inside("-v /var/run/docker.sock:/var/run/docker.sock:rw -u $JENNY_DOCKER_UID --group-add $JENNY_DOCKER_GID${extraMount}") {
                    checkout scm
                    try {
                        sh """
                            bin/test_jenny.py
                        """
                    } finally {
                        sh "cat /tmp/jenny_*.log"
                    }
        }
    }
}

if (isMasterBranch()) {
    stage('Publish on github') {
        node {
            deleteDir()
            checkout scm

            publishGit repo: "git@github.com:bmustiata/jenny.git"
        }
    }
}
