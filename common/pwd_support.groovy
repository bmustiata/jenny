def _tempFolder = "/tmp/"  // FIXME: something smarter?

pwd = { tmp = false -> 
    if (tmp) {
        return _tempFolder.canonicalPath
    }

    return System.getProperty("user.dir")
}
