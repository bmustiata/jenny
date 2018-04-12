dir = { path, code ->
    _log.message(_currentIndent("dir ${path}"))

    _increaseIndent {
        _runInFolder(new File(path), code, create=false, ignoreMissing=true)
    }
}
