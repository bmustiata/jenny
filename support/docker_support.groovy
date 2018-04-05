class DockerAgent {
    def context
    String containerId

    void sh(String code) {
        context._executeProcess.call(
            'docker', 'exec', '-t', this.containerId,
            'bash', '-c', code)
    }

    void deleteDir() {
        context._executeProcess.call(
            'docker', 'exec', '-t', this.containerId,
            'rm', '-fr', context.pwd())
    }

    void checkout(version) {
        context._executeProcess.call(
            'docker', 'exec', '-t', this.containerId,
            'mkdir', '-p', context.pwd())
        context._executeProcess.call(
            'docker', 'exec', '-t', this.containerId,
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
            throw new IllegalStateException("Process execution failed, exit code: " + exitCode)
        }

        return new DockerAgent(
            context: context,
            containerId: process.inputStream.text.trim()
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
