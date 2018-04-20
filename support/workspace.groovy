/**
 * Creates a new workspace for the execution.
 */
_prepareWorkspace = { ->
    def projectFolderName = _jennyConfig.projectFolder.name
    def workspaceLocation = new File("${_jennyConfig.workFolder}/jenny/workspace/${projectFolderName}/workspace")

    _log.message("> workspace: ${workspaceLocation.canonicalPath}")

    workspaceLocation.deleteDir()
    if (!workspaceLocation.mkdirs()) {
        throw new IllegalStateException("Unable to create workspace: ${workspaceLocation.canonicalPath}")
    }

    return workspaceLocation
}
