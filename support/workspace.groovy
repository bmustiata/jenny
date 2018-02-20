/**
 * Creates a new workspace for the execution.
 */
_prepareWorkspace = { ->
    def workspaceLocation = new File("/tmp/.jenny/workspace/")
    workspaceLocation.deleteDir()
    workspaceLocation.mkdir()

    return workspaceLocation
}
