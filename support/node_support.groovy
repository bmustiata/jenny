class NodeAgent {
    def context
    String id

    String getNodeId() {
        id
    }

    void sh(String code) {
        context._log.message("> node::sh ----------------------------------")
        context._log.message(code)
        context._log.message("> -------------------------------------------")

        context._executeProcess.call(null, 'sh', '-c', code)
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

        org.apache.commons.io.FileUtils.copyDirectory(
            context._jennyConfig.projectFolder,
            new File(pwd()))
    }

    void copyToLocal(String source, String destination) {
        context._executeProcess.call(null, 'cp', source, destination)
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
            _currentAgent = new NodeAgent(
                context: binding,
                id: fullId
            )
            def nodeFolder = new File(_jennyConfig.workspaceFolder, "../" + fullId)
            _runInFolder.call(nodeFolder, code, create=true, ignoreMissing=_jennyConfig.info)
        } finally {
            _currentAgent = currentAgent
        }
    }
}
