def _currentFolder = _jennyConfig.workspaceFolder
def _tempFolder = "/tmp/"  // FIXME: something smarter?

pwd = { tmp = false -> 
    if (tmp) {
        return _tempFolder.canonicalPath
    }

    return _currentFolder?.canonicalPath ?: _jennyConfig.workspaceFolder
}
