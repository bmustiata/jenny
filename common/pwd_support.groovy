def _tempFolder = "${_jennyConfig.workFolder}/"  // FIXME: something smarter?

pwd = { tmp = false -> 
    if (tmp) {
        return _tempFolder.canonicalPath
    }

    return System.getProperty("user.dir")
}
