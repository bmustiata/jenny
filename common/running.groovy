_runInFolder = { File filePath, code, create=false, ignoreMissing=false ->
    def currentPath = System.getProperty("user.dir")
    def currentPwd = env.PWD

    try {
        System.setProperty("user.dir", filePath.canonicalPath)
        env.PWD = filePath.canonicalPath

        def fileExists = filePath.exists()
        if (!fileExists && !create && !ignoreMissing) {
            throw new IllegalStateException("'${filePath.canonicalPath}' does not exist.")
        }

        if (!fileExists && !ignoreMissing && !filePath.mkdirs()) {
            throw new IllegalStateException("Unable to create '${filePath.canonicalPath}'.")
        }

        code()
    } finally {
        env.PWD = currentPwd
        System.setProperty("user.dir", currentPath)
    }
}
