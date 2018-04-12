def call() {
    sh """
    echo "pwd from pluginCommand: `pwd`"
    """
    _log.message new File(".").canonicalPath
    _log.message System.getProperty("user.dir")
}

