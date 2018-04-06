class NodeAgent {
    def context

    void sh(String code) {
        println("> node::sh ----------------------------------")
        println(code)
        println("> -------------------------------------------")

        context._executeProcess.call('sh', '-c', code)
    }

    void deleteDir() {
        println("node::deleteDir ${pwd()}")
        new File(pwd()).deleteDir()        
    }

    void checkout(String version) {
        println("node::checkout ${version}")

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

    // FIXME: the pwd from common is not accessible
    String pwd() {
        return System.getProperty("user.dir")
    }
}

node = { name = null, code ->
    _runSectionWithId("node") { fullId ->
        def currentAgent = _currentAgent
        try {
            _currentAgent = new NodeAgent(context: binding)
            def nodeFolder = new File(_jennyConfig.workspaceFolder, "../" + fullId)
            _runInFolder.call(nodeFolder, code, create=true, ignoreMissing=_jennyConfig.info)
        } finally {
            _currentAgent = currentAgent
        }
    }
}
