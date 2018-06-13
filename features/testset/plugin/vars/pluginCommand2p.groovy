def call(param1, param2) {
    sh """
    echo "pwd from pluginCommand2p: `pwd`"
    """
    _log.message new File(".").canonicalPath
    _log.message System.getProperty("user.dir")
}

