class DockerAgent {
    def context
    String containerId

    void sh(String code) {
        println("> docker::sh --------------------------------")
        println(code)
        println("> -------------------------------------------")

        def scriptPath = "/tmp/${UUID.randomUUID() as String}.sh"

        new File(scriptPath).write(code)

        context._executeProcess.call(
            'docker', 'cp', scriptPath, "${this.containerId}:${scriptPath}"
        )
        context._executeProcess.call(
            'docker', 'exec', '-t', this.containerId,
            'sh', '-c', "cd ${pwd()}; . ${scriptPath}")
    }

    void deleteDir() {
        println("docker::deleteDir ${context.pwd()}")

        context._executeProcess.call(
            'docker', 'exec', '-t', this.containerId,
            'rm', '-fr', context.pwd())
    }

    void checkout(version) {
        println("docker::checkout ${version}")

        context._executeProcess.call(
            'docker', 'exec', '-t', this.containerId,
            'mkdir', '-p', context.pwd())
        context._executeProcess.call(
            'docker', 'exec', '-t', this.containerId,
            'cp', '-R', context._jennyConfig.projectFolder, context.pwd())
    }

    void shutdown() {
        context._executeProcessSilent.call(
            'docker', 'rm', '-f', this.containerId)
    }

    String pwd() {
        return System.getProperty("user.dir")
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

    void inside(Closure code) {
        this.inside(null, code)
    }

    void inside(String parameters, Closure code) {
        def currentAgent = context._currentAgent
        def dockerAgent
        try {
            dockerAgent = this.startDockerAgent(this.imageName, parameters)
            context._currentAgent = dockerAgent
            code.call()
        } finally {
            dockerAgent.shutdown()
            context._currentAgent = currentAgent
        }
    }

    void withRun(Closure code) {
        this.withRun(null, code)
    }

    void withRun(String parameters, Closure code) {
        code.call()
    }

    DockerAgent startDockerAgent(imageName, parameters) {
        def command = ["docker", "run", "-t",
                                "-d", 
                                "-u", "1000:1000", // FIXME: really read it
                                "--entrypoint", "cat",
                                "-w", System.getProperty("user.dir"), // FIXME: pwd not accessible
                                // this is only needed for checkouts, don't allow rw access
                                "-v", "${context._jennyConfig.projectFolder}:${context._jennyConfig.projectFolder}:ro"
                        ]

        context.env.each { k, v -> 
            command.add("-e")
            command.add("${k}=${v}")
        }

        if (parameters) {
            parameters.split(" ").each{ command.add it }
        }

        command.add(imageName)

        def processBuilder = new ProcessBuilder(command as String[])
            .directory(new File(System.getProperty("user.dir")))

        def process = processBuilder.start()
        def exitCode = process.waitFor();
        
        if (exitCode != 0) {
            throw new IllegalStateException(
                """\
                Process execution failed, exit code: ${exitCode},
                command `${command.join(' ')}
                STDOUT:\n${process.inputStream.text}
                STDERR:\n${process.errorStream.text}
                """.stripIndent())
        }

        return new DockerAgent(
            context: context,
            containerId: process.inputStream.text.trim()
        )
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

        context._executeProcess(command as String[])

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
