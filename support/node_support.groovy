class NodeAgent {
    def context
    String id
    String agentWorkFolder

    String getNodeId() {
        id
    }

    Object sh(config) {
        if (!(config instanceof Map)) {
            config = [
                script: config as String
            ]
        }

        context._log.message("> node::sh ----------------------------------")
        context._log.message(config.script)
        context._log.message("> -------------------------------------------")

        return context._executeReturnProcess.call([
            cwd: null,
            args: ['sh', '-c', config.script],
            returnStatus: config.returnStatus,
            returnStdout: config.returnStdout
        ])
    }

    void mkdir(name) {
        context._log.message("node::mkdir ${name}")
        new File(name).mkdirs()
    }

    void deleteDir() {
        context._log.message("node::deleteDir ${pwd()}")
        new File(pwd()).deleteDir()
    }

    void checkout(String version) {
        context._log.message("node::checkout ${version}")

        if (version != "SCM") {
            throw new IllegalArgumentException("Only SCM checkout is supported.")
        }

        if (!new File(pwd()).exists()) {
            new File(pwd()).mkdirs()
        }

        context._executeProcess.call(null, 'sh', '-c', "cp -R '${context._jennyConfig.projectFolder}/.' '${pwd()}'")
    }

    void copyToLocal(String source, String destination) {
        context._executeProcess.call(null, 'bash', '-c', "shopt -s globstar; cd '${pwd()}'; cp -R ${source} ${destination}")
    }

    void copyToAgent(String source, String destination) {
        context._executeProcess.call(null, 'bash', '-c', "shopt -s globstar; cd '${pwd()}'; cp -R ${source} ${destination}")
    }

    // FIXME: the pwd from common is not accessible
    String pwd() {
        return System.getProperty("user.dir")
    }
}

node = { name = null, code ->
    _runSectionWithId("node") { fullId ->
        def currentAgent = _currentAgent
        try {
            def nodeFolder = new File(_jennyConfig.workspaceFolder, "../" + fullId)

            _currentAgent = new NodeAgent(
                context: binding,
                id: fullId,
                agentWorkFolder: nodeFolder.canonicalPath
            )

            _runInFolder.call(nodeFolder, code, create=true, ignoreMissing=_jennyConfig.info)
        } finally {
            _currentAgent = currentAgent
        }
    }
}
