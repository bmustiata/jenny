def loadLibrary(shell, binding, path) {
    def jennyConfig = binding._jennyConfig

    if (jennyConfig.verbose) {
        _log.message "> Loading library ${path}"
    }

    def libFolder = new File(path, "vars")

    if (!libFolder.exists()) {
        throw new IllegalArgumentException("${libFolder} does not exists")
    }

    libFolder.listFiles()
        .findAll { it.name.endsWith(".groovy") }
        .each { File commandFile ->
            registerCommandInBinding(shell, binding, commandFile)
        }
}

def loadInfoLibrary(shell, binding, path) {
    def jennyConfig = binding._jennyConfig

    if (jennyConfig.verbose) {
        _log.message "> Loading library ${path}"
    }

    def libFolder = new File(path, "vars")

    if (!libFolder.exists()) {
        throw new IllegalArgumentException("${libFolder} does not exists")
    }

    libFolder.listFiles()
        .findAll { it.name.endsWith(".groovy") }
        .each { commandFile ->
            def commandName = commandFile.getName().substring(0, commandFile.getName().lastIndexOf("."))

            if (isCommandAllowed(binding, commandName)) {
                registerInfoCommandInBinding(shell, binding, commandFile)
            } else {
                binding[commandName] = { Object...config ->
                    shell.context._log.message.call(shell.context._currentIndent.call(commandName))
                }
            }
        }
}

def isCommandAllowed(binding, commandName) {
    for (def expression: binding._jennyConfig.libInfoAllowed) {
        if (commandName.matches(expression)) {
            return true
        }
    }

    return false
}

def registerCommandInBinding(shell, binding, commandFile) {
    def command = shell.parse(commandFile)
    def commandName = commandFile.getName().substring(0, commandFile.getName().lastIndexOf("."))

    binding[command.class.name] = { Object... config ->
        shell.context._log.message.call(commandName)
        return command.invokeMethod("call", config)
    }
}

def registerInfoCommandInBinding(shell, binding, commandFile) {
    def command = shell.parse(commandFile)
    def commandName = commandFile.getName().substring(0, commandFile.getName().lastIndexOf("."))

    binding[command.class.name] = { Object... config ->
        shell.context._log.message.call(shell.context._currentIndent.call(commandName))
        return shell.context._increaseIndent.call {
            return command.invokeMethod("call", config)
        }
    }
}

loadLibraries = { shell, binding ->
    binding._jennyConfig.libs.each {
        loadLibrary(shell, binding, (it as File).isAbsolute() ?
            it:
            new File(binding._jennyConfig.projectFolder, it).canonicalPath)
    }
}

loadInfoLibraries = { shell, binding ->
    binding._jennyConfig.libs.each {
        loadInfoLibrary(shell, binding, (it as File).isAbsolute() ?
            it:
            new File(binding._jennyConfig.projectFolder, it).canonicalPath)
    }
}
