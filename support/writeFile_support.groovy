import java.nio.file.Files

writeFile = { config ->
    _log.message("> writeFile: ${config.file}")

    if (!config || !config.file || !config.text) {
        throw new IllegalArgumentException(
            "You need to pass a config to `writeFile` with a `file`, and a `text` attribute.")
    }

    tempFile = new File(Files.createTempFile(null, null).toAbsolutePath().toString())
    tempFile.write(config.text)

    _currentAgent.copyToAgent(tempFile.canonicalPath, "${pwd()}/${config.file}")

    tempFile.delete()
}

