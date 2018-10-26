@NonCPS
def call(param1) {
    sh """
    echo "pwd from pluginCommand1p: `pwd`"
    """
    _log.message new File(".").canonicalPath
    _log.message System.getProperty("user.dir")
}

