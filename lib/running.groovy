runInFolder = { File filePath, code ->
    def currentPath = System.getProperty("user.dir")

    try {
        System.setProperty("user.dir", filePath.absolutePath)

        code()
    } finally {
        System.setProperty("user.dir", currentPath)
    }
}