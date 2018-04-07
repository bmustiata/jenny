dir = { path, code ->
    runInFolder(new File(path), code, create=false, ignoreMissing=true)
}
