writeFile = { config ->
    if (!config || !config.file || !config.text) {
        throw new IllegalArgumentException(
            "You need to pass a config to `writeFile` with a `file`, and a `text` attribute.")
    }


    _log.message(_currentIndent("writeFile: ${config.file}"))
}

