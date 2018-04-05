_executeProcess = { String ... args ->
    print("execute process: ${args.join(' ')}")
    def process = new ProcessBuilder(args)
        .directory(new File(pwd()))
        .inheritIO()

    process.environment().putAll(env)

    def exitCode = process.start().waitFor()

    if (exitCode != 0) {
        throw new IllegalStateException("Process execution failed, exit code: " + exitCode)
    }
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
