/**
 * Creates a new workspace for the execution.
 */
_prepareWorkspace = { ->
    def projectFolderName = _jennyConfig.projectFolder.name
    def workFolder = _jennyConfig.workFolder

    def workspaceLocation = new File("${workFolder}/jenny/workspace/${projectFolderName}/workspace")
    _jennyConfig.workspaceFolder = workspaceLocation

    _log.message(_currentIndent("> workspace: ${workspaceLocation.canonicalPath}"))

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

    _jennyConfig.archiveFolder = archiveLocation.canonicalPath

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

    _jennyConfig.junitFolder = junitLocation.canonicalPath

    return _jennyConfig
}
