def loadLibrary(shell, binding, path) {
    println "> Loading library ${path}"
    def libFolder = new File(path, "vars")

    if (!libFolder.exists()) {
        throw new IllegalArgumentException("${libFolder} does not exists")
    }

    libFolder.listFiles().each { commandFile ->
        def command = shell.parse(commandFile)
        binding[command.class.name] = { config = null ->
            return command.invokeMethod("call", config)
        }
    }
}

def loadInfoLibrary(shell, binding, path) {
    println "> Loading library ${path}"
    def libFolder = new File(path, "vars")

    if (!libFolder.exists()) {
        throw new IllegalArgumentException("${libFolder} does not exists")
    }

    libFolder.listFiles().each { commandFile ->
        def commandName = commandFile.getName().substring(0, commandFile.getName().lastIndexOf("."))
        binding[commandName] = { config = null ->
            shell.evaluate("println(_currentIndent('${commandName}'))")
        }
    }
}

loadLibraries = { shell, binding ->
    binding._jennyConfig.libs.each {
        loadLibrary(shell, binding, it)
    }
}

loadInfoLibraries = { shell, binding ->
    binding._jennyConfig.libs.each {
        loadInfoLibrary(shell, binding, it)
    }    
}
