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
    }

    void deleteDir() {
        context._log.message("docker::deleteDir ${pwd()}")
    }

    void checkout(version) {
        context._log.message("docker::checkout ${version}")
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
    }

    String port(int port) {
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
        context._log.message(context._currentIndent.call("docker::inside ${imageName}"))
        context._increaseIndent.call {
            def currentAgent = context._currentAgent
            def dockerAgent

            try {
                dockerAgent = this.startDockerContainer(args, command)

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
    }

    void withRun(String args=null, String command=null, Closure code) {
        context._log.message(context._currentIndent.call("docker::withRun ${imageName}"))
        context._increaseIndent.call {
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
        def id = (UUID.randomUUID() as String).substring(0, 8)

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

