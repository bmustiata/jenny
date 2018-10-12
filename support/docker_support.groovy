class DockerAgent {
    def context
    String id
    String agentWorkFolder

    String getNodeId() {
        id.substring(0, 8)
    }

    Object sh(config) {
        if (!(config instanceof Map)) {
            config = [
                script: config as String
            ]
        }

        context._log.message("> docker::sh --------------------------------")
        context._log.message(config.script)
        context._log.message("> -------------------------------------------")

        def workFolder = context._jennyConfig.workFolder
        def scriptPath = "${workFolder}/${UUID.randomUUID() as String}.sh"

        new File(scriptPath).write(config.script)

        context._executeProcess.call(
            "/", // cwd on host
            'docker', 'cp', scriptPath, "${this.id}:${scriptPath}"
        )

        return context._executeReturnProcess.call([
            cwd: "/", // cwd on host
            args: ['docker', 'exec', '-t', this.id, 'sh', '-c', "cd ${pwd()}; . ${scriptPath}"],
            returnStatus: config.returnStatus,
            returnStdout: config.returnStdout
        ])
    }

    void mkdir(name) {
        context._log.message("docker::mkdir ${name}")
        context._executeProcess.call(
            '/', // cwd on host
            'docker', 'exec', '-t', this.id,
            'mkdir', '-p', name)
    }

    void deleteDir() {
        context._log.message("docker::deleteDir ${pwd()}")

        context._executeProcess.call(
            '/', // cwd on host
            'docker', 'exec', '-t', this.id,
            'rm', '-fr', pwd())
    }

    void checkout(version) {
        context._log.message("docker::checkout ${version}")

        def folder = pwd()

        context._executeProcess.call(
            '/', // cwd on host
            'docker', 'cp',
            "${context._jennyConfig.projectFolder.canonicalPath}/.",
            "${id}:${folder}/")
    }

    void copyToLocal(String source, String destination) {
        String absoluteSource = source

        if (!isAbsolutePath(source)) {
            absoluteSource = "${pwd()}/${source}"
        }

        // This is a workaround since docker doesn't support wildchar copy
        // Normally this should be just:
        // context._executeProcess.call(
        //    '/', // cwd on host
        //    'docker', 'cp',
        //    "${id}:${absoluteSource}",
        //    "${destination}/")

        context._executeProcess.call(
            '/', // cwd on host
            'docker', 'exec', id,
            'bash', '-c',
            "mkdir /tmp/_jenny_extract; cp ${absoluteSource} /tmp/_jenny_extract"
        )

        context._executeProcess.call(
            '/', // cwd on host
            'docker', 'cp',
            "${id}:/tmp/_jenny_extract",
            "${destination}")

        context._executeProcess.call(
            '/', // cwd on host
            'docker', 'exec', id,
            'rm', '-fr', '/tmp/_jenny_extract'
        )

        context._executeProcess.call(
            '/', // cwd on host
            'bash', '-c',
            "mv ${destination}/_jenny_extract/* ${destination}; rmdir ${destination}/_jenny_extract"
        )
    }

    void copyToAgent(String source, String destination) {
        String absoluteSource = source

        if (!isAbsolutePath(source)) {
            absoluteSource = "${pwd()}/${source}"
        }

        context._executeProcess.call(
            '/', // cwd on host
            'bash', '-c',
            "docker cp ${absoluteSource} ${id}:${destination}")
    }

    boolean isAbsolutePath(String path) {
        return path.startsWith("/")
    }

    String pwd() {
        return System.getProperty("user.dir")
    }

    Container getContainer() {
        return new Container(
            context: context,
            id: id
        )
    }
}

class Container {
    def context
    String id

    void stop() {
        context._executeProcessSilent.call(
            '/', // cwd on host
            'docker', 'rm', '-f', this.id)
    }

    String port(int port) {
        return context._executeProcessSilent.call(
            '/', // cwd on host
            'docker', 'port', this.id, port as String)
    }
}

/**
 * Runs a container starting from an image. If inside it's executed,
 * this will create and switch the agent to the docker one, while
 * running commands inside the agent.
 */
class DockerImage {
    def context
    String imageName

    void inside(String args=null, String command=null, Closure code) {
        context._log.message("docker::inside ${imageName}")
        def currentAgent = context._currentAgent
        def dockerAgent

        try {
            if (args) {
                args = "${args} --entrypoint cat"
            } else {
                args = "--entrypoint cat"
            }

            dockerAgent = this.startDockerContainer(args, command)

            context._executeProcess.call( // prepare the workspace
                '/', // cwd on host
                'docker', 'exec', '-t', dockerAgent.id,
                'mkdir', '-p', pwd())


            context._currentAgent = dockerAgent
            code.call(dockerAgent.container)
        } catch (Exception e) {
            throw new IllegalArgumentException("Failure executing code inside container with image ${imageName}: ${e.message}", e)
        } finally {
            dockerAgent && dockerAgent.container.stop()
            context._currentAgent = currentAgent
        }
    }

    void withRun(String args=null, String command=null, Closure code) {
        context._log.message("docker::withRun ${imageName}")
        def currentAgent = context._currentAgent
        def dockerAgent = null

        try {
            dockerAgent = this.startDockerContainer(args, command)
            context._currentAgent = dockerAgent
            code.call(dockerAgent.container)
        } catch (Exception e) {
            throw new IllegalArgumentException("Failure running container with image ${imageName}: ${e.message}", e)
        } finally {
            dockerAgent.container.stop()
            context._currentAgent = currentAgent
        }
    }

    /**
     * Start a container.
     */
    Container run(String args=null, String command=null) {
        context._log.message("docker::run ${imageName}")
        def dockerAgent = this.startDockerContainer(args, command)
        return new Container(
            id: dockerAgent.id,
            context: context
        )
    }

    private DockerAgent startDockerContainer(args, parameters) {
        def command = ["docker", "run", "-t",
                                 "-u", "${System.getProperty('jenny_userid')}:${System.getProperty('jenny_groupid')}",
                                 "-d",
                                 // this is only needed for checkouts, don't allow rw access
                                 //"-v", "${context._jennyConfig.projectFolder}:${context._jennyConfig.projectFolder}:ro"
                        ]

        context.env.each { k, v ->
            command.add("-e")
            command.add("${k}=${v}")
        }

        if (args) {
            args.split(" ").each{ command.add it }
        }

        command.add(imageName)

        if (parameters) {
            parameters.split(" ").each{ command.add it }
        }

        def id = context._executeProcessSilent.call(
                        "/", // cwd on host
                        command as String[])

        return new DockerAgent(
            context: context,
            agentWorkFolder: pwd(),
            id: id
        )
    }

    String pwd() {
        System.getProperty("user.dir")
    }
}

/**
 * Allow running a build for a container, and potentially
 * run things after in it via the available image.
 */
class DockerBuild {
    def context
    def imageName
    def parameters

    DockerImage build() {
        context._log.message("docker::build ${imageName}")
        def command = ["docker", "build", "-t", imageName]

        if (parameters) {
            command.addAll(parameters.split(" "))
        }

        context._executeProcess.call(
                null, // don't change cwd on host
                command as String[])

        return new DockerImage(
            context: context,
            imageName: imageName
        )
    }
}

/**
 * Docker entry point container.
 */
class DockerBuilder {
    def context

    public DockerImage image(String name) {
        return new DockerImage(
            context: context,
            imageName: name
        )
    }

    public DockerImage build(String name) {
        return this.build(name, ".")
    }

    public DockerImage build(String name, String parameters) {
        return new DockerBuild(
            context: context,
            imageName: name,
            parameters: parameters
        ).build()
    }
}

docker = new DockerBuilder(context: binding)

