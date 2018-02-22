runInFolder = { File filePath, code ->
    def currentPath = System.getProperty("user.dir")

    try {
        System.setProperty("user.dir", filePath.canonicalPath)

        code()
    } finally {
        System.setProperty("user.dir", currentPath)
    }
}