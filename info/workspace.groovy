/**
 * Creates a new workspace for the execution.
 */
_prepareWorkspace = { ->
    def projectFolderName = _jennyConfig.projectFolder.name
    def workFolder = _jennyConfig.workFolder

    def workspaceLocation = new File("${workFolder}/jenny/workspace/${projectFolderName}/workspace")

    _log.message(_currentIndent("> workspace: ${workspaceLocation.canonicalPath}"))

    return workspaceLocation
}
