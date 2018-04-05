class DockerAgent {
    def context
    String containerId

    void sh(String code) {
        _executeProcess('docker', 'exec', '-t', this.containerId,
                        'sh', '-c', code)
    }

    void deleteDir() {
        _executeProcess('docker', 'exec', '-t', this.containerId,
                        'rm', '-fr', context.pwd())
    }

    void checkout(version) {
        _executeProcess('docker', 'exec', '-t', this.containerId,
                        'mkdir', '-p', context.pwd())
        _executeProcess('docker', 'exec', '-t', this.containerId,
                        'cp', '-R', context._jennyConfig.projectFolder, context.pwd())
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
        try {
            context._currentAgent = this.startDockerAgent(this.imageName, parameters)
            code.call()
        } finally {
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
                                "-w", context.pwd(),
                                // this is only needed for checkouts, don't allow rw access
                                "-v", "${_jennyConfig.projectFolder}:${_jennyConfig.projectFolder}:ro"
                        ]

        env.each { k, v -> 
            command.add("-e")
            command.add("${k}=${v}")
        }

        if (parameters) {
            parameters.split(" ").each
        }

        command.add(imageName)

        def process = new ProcessBuilder(args)
            .directory(new File(context.pwd()))

        def inputStream = process.inputStream
        def exitCode = process.start().waitFor();

        if (exitCode != 0) {
            throw new IllegalStateException("Process execution failed, exit code: " + exitCode)
        }

        return new DockerAgent(
            context: binding,
            containerId: inputStream.text.trim()
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
}

docker = new DockerBuilder(context: binding)
