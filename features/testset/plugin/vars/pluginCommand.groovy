def call() {
    sh """
    echo "pwd from pluginCommand: `pwd`"
    """
    println new File(".").canonicalPath
    println System.getProperty("user.dir")
}

