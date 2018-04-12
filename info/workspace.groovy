/**
 * Creates a new workspace for the execution.
 */
_prepareWorkspace = { ->
    def projectFolderName = _jennyConfig.projectFolder.name
    def workspaceLocation = new File("/tmp/jenny/workspace/${projectFolderName}/workspace")

    _log.message(_currentIndent("> workspace: ${workspaceLocation.canonicalPath}"))

    return workspaceLocation
}
