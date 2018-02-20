/**
 * Creates a new workspace for the execution.
 */
_prepareWorkspace = { ->
    def projectFolderName = _jennyConfig.projectFolder.name
    def workspaceLocation = new File("/tmp/jenny/workspace/${projectFolderName}")

    println("> workspace: ${workspaceLocation.canonicalPath}")

    workspaceLocation.deleteDir()
    if (!workspaceLocation.mkdirs()) {
        throw new IllegalStateException("Unable to create workspace: ${workspaceLocation.canonicalName}")
    }

    return workspaceLocation
}
