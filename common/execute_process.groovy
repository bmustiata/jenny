_executeProcess = { String... args ->
    println("exec: ${args.join(' ')}")
    def processBuilder = new ProcessBuilder(args)
        .directory(new File(pwd()))
        .inheritIO()

    processBuilder.environment().putAll(env)

    def process = processBuilder.start()
    def exitCode = process.waitFor()

    if (exitCode != 0) {
        throw new IllegalStateException(
            """\
            Process execution failed, exit code: ${exitCode},
            command `${args}`
            STDOUT:\n${process.inputStream.text}
            STDERR:\n${process.errorStream.text}
            """.stripIndent())
    }
}

_executeProcessSilent = { String... args ->
    println("exec silent: ${args.join(' ')}")
    def processBuilder = new ProcessBuilder(args)
        .directory(new File(pwd()))

    processBuilder.environment().putAll(env)

    def process = processBuilder.start()
    def exitCode = process.waitFor()

    if (exitCode != 0) {
        throw new IllegalStateException(
            """\
            Process execution failed, exit code: ${exitCode},
            command `${args}`
            STDOUT:\n${process.inputStream.text}
            STDERR:\n${process.errorStream.text}
            """.stripIndent())
    }

    return process.inputStream.text.trim()
}


class NoopAgent {
    void sh(String code) {
        throw new Exception("You need to be inside a node, or on an agent for `sh` to work.")
    }

    void deleteDir() {
        throw new Exception("You need to be inside a node, or on an agent for `deleteDir` to work.")
    }

    void checkout(version) {
        throw new Exception("You need to be inside a node, or on an agent for `checkout` to work.")
    }
}

_currentAgent = new NoopAgent()
