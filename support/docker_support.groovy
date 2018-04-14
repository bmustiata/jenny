class DockerAgent {
    def context
    String id

    String getNodeId() {
        id.substring(0, 8)
    }

    void sh(String code) {
        context._log.message("> docker::sh --------------------------------")
        context._log.message(code)
        context._log.message("> -------------------------------------------")

        def scriptPath = "/tmp/${UUID.randomUUID() as String}.sh"

        new File(scriptPath).write(code)

        context._executeProcess.call(
            "/", // cwd on host
            'docker', 'cp', scriptPath, "${this.id}:${scriptPath}"
        )
        context._executeProcess.call(
            "/", // cwd on host
            'docker', 'exec', '-t', this.id,
            'sh', '-c', "cd ${pwd()}; . ${scriptPath}")
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
            context._log.message("ERROR: " + e.getMessage())
            e.printStackTrace()
        } finally {
            dockerAgent && dockerAgent.container.stop()
            context._currentAgent = currentAgent
        }
    }

    void withRun(String args=null, String command=null, Closure code) {
        def currentAgent = context._currentAgent
        def dockerAgent = null

        try {
            dockerAgent = this.startDockerContainer(args, command)
            context._currentAgent = dockerAgent
            code.call(dockerAgent.container)
        } catch (Exception e) {
            _log.message("ERROR: " + e.getMessage())
            e.printStackTrace()
        } finally {
            dockerAgent.container.stop()
            context._currentAgent = currentAgent
        }
    }

    /**
     * Start a container.
     */
    Container run(String args=null, String command=null) {
        def dockerAgent = this.startDockerContainer(args, command)
        return new Container(
            id: dockerAgent.id,
            context: context
        )
    }

    private DockerAgent startDockerContainer(args, parameters) {
        def command = ["docker", "run", "-t",
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

