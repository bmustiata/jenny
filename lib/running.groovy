runInFolder = { File filePath, code, create=false ->
    def currentPath = System.getProperty("user.dir")

    try {
        System.setProperty("user.dir", filePath.canonicalPath)

        def fileExists = filePath.exists()
        if (!fileExists && !create) {
            throw new IllegalStateException("'${filePath.canonicalPath}' does not exist.")
        }

        if (!fileExists && !filePath.mkdirs()) {
            throw new IllegalStateException("Unable to create '${filePath.canonicalPath}'.")
        }

        code()
    } finally {
        System.setProperty("user.dir", currentPath)
    }
}