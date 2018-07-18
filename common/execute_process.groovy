_executeProcess = { String cwd, String... args ->
    _parentLog.logMessage("exec: ${args.join(' ')}")

    def currentPath = System.getProperty("user.dir")

    if (cwd == null) {
        cwd = currentPath;
    }

    try {
        System.setProperty("user.dir", cwd)

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
    } finally {
        System.setProperty("user.dir", currentPath)
    }
}

_executeProcessSilent = { String cwd, String... args ->
    _parentLog.logMessage("exec silent: ${args.join(' ')}")

    def currentPath = System.getProperty("user.dir")

    if (cwd == null) {
        cwd = currentPath;
    }

    try {
        System.setProperty("user.dir", cwd)

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
    } finally {
        System.setProperty("user.dir", currentPath)
    }

}


class NoopAgent {
    String getNodeId() {
        return "<host>"
    }

    void sh(String code) {
        throw new Exception("You need to be inside a node, or on an agent for `sh` to work.")
    }

    void deleteDir() {
        throw new Exception("You need to be inside a node, or on an agent for `deleteDir` to work.")
    }

    void checkout(version) {
        throw new Exception("You need to be inside a node, or on an agent for `checkout` to work.")
    }

    void copyToLocal(String source, String destination) {
        throw new Exception("You need to be inside a node, or on an agent for `copy` to work.")
    }

    void copyToAgent(String source, String destination) {
        throw new Exception("You need to be inside a node, or on an agent for `copy` to work.")
    }
}

_currentAgent = new NoopAgent()

