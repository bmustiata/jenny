node = { name = null, code ->
    _runSectionWithId("node") { fullId ->
        def nodeFolder = new File(_jennyConfig.workspaceFolder, "../" + fullId)
        _runInFolder.call(nodeFolder, code, create=true)
    }
}
