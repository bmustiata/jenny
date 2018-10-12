deleteDir = { ->
    _currentAgent.deleteDir()

    // deleteDir in Jenkins doesn't delete the top folder of the node/workspace
    if (pwd() == _currentAgent.agentWorkFolder) {
        _currentAgent.mkdir(pwd())
    }
}
