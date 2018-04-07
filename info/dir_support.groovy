dir = { path, code ->
    println(_currentIndent("dir ${path}"))

    _increaseIndent {
        _runInFolder(new File(path), code, create=false, ignoreMissing=true)
    }
}
