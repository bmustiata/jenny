def _currentFolder = _jennyConfig.workspaceFolder
def _tempFolder = "/tmp/"  // FIXME: something smarter?

pwd = { tmp = false -> 
    if (tmp) {
        return _tempFolder.canonicalPath
    }

    return _currentFolder?.canonicalPath ?: 
           _jennyConfig.workspaceFolder.canonicalPath
}

_runInFolder = { File filePath, code ->
    def currentPath = System.getProperty("user.dir")

    try {
        System.setProperty("user.dir", filePath.absolutePath)

        code()
    } finally {
        System.setProperty("user.dir", currentPath)
    }
}