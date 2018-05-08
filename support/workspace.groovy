
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

    def archiveLocation = new File(workspaceLocation, "archive")

    if (_jennyConfig.archiveFolder) {
        archiveLocation = new File(_jennyConfig.archiveFolder)

        if (!archiveLocation.absolute) {
            archiveLocation = new File(_jennyConfig.projectFolder, _jennyConfig.archiveFolder)
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

    return _jennyConfig
}


