dir = { path, code ->
    _runInFolder(new File(path), code, create=false, ignoreMissing=true)
}
