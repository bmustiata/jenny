
_mkdirp = { path ->
    if (path.exists() && path.directory) {
        return true
    }


    return path.mkdirs()
}

/**
 * Creates a new workspace for the execution.
 */
_prepareWorkspace = { ->
    def projectFolderName = _jennyConfig.projectFolder.name
    def workspaceLocation = new File("${_jennyConfig.workFolder}/jenny/workspace/${projectFolderName}/workspace")

    _jennyConfig.workspaceFolder = workspaceLocation

    // -------------------------------------------------------------------
    // archiveLocation
    // -------------------------------------------------------------------
    def archiveLocation = new File(workspaceLocation, "archive")

    if (_jennyConfig.archiveFolder) {
        archiveLocation = new File(_jennyConfig.archiveFolder)

        if (!archiveLocation.absolute) {
            archiveLocation = new File(_jennyConfig.projectFolder, _jennyConfig.archiveFolder)
        }
    }

    // -------------------------------------------------------------------
    // junitLocation
    // -------------------------------------------------------------------
    def junitLocation = new File(workspaceLocation, "junit")

    if (_jennyConfig.junitFolder) {
        junitLocation = new File(_jennyConfig.junitFolder)

        if (!junitLocation.absolute) {
            junitLocation = new File(_jennyConfig.projectFolder, _jennyConfig.junitFolder)
        }
    }


    _log.message("> workspace: ${workspaceLocation.canonicalPath}")

    workspaceLocation.deleteDir()
    if (!workspaceLocation.mkdirs()) {
        throw new IllegalStateException("Unable to create workspace: ${workspaceLocation.canonicalPath}")
    }

    _jennyConfig.archiveFolder = archiveLocation.canonicalPath

    if (!_mkdirp(archiveLocation)) {
        throw new IllegalStateException("Unable to create archive folder: ${archiveLocation.canonicalPath}")
    }

    _jennyConfig.junitFolder = junitLocation.canonicalPath

    if (!_mkdirp(junitLocation)) {
        throw new IllegalStateException("Unable to create archive folder: ${junitLocation.canonicalPath}")
    }

    return _jennyConfig
}


