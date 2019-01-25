sh = { config ->
    if (!(config instanceof Map)) {
        config = [
            script: config as String
        ]
    }

    def code = config.script.stripIndent().trim().replaceAll("\n", "; ")

    if (code.length() > 100) {
        code = code.substring(0, 10) + " .. " + code.substring(code.length() - 86)
    }

    _log.message(_currentIndent("sh: ${code}"))

    if (config.returnStdout) {
        return ""
    }

    if (config.returnStatus) {
        return 0
    }
}
