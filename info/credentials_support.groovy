class CredentialFile {
    String credentialsId
    String variable
}

def searchLocations = [_jennyConfig.jennyGlobalConfigFolder, ".jenny"]

withCredentials = { files, code ->
    _log.message(_currentIndent("withCredentials"))
    files.each { credentialFile ->
        for (def searchLocation: searchLocations) {
            def file = new File("${searchLocation}/credentials/${credentialFile.credentialsId}")

            if (file.exists()) {
                def targetName = "/tmp/${credentialFile.credentialsId}-${UUID.randomUUID() as String}"

                //_currentAgent.copyToAgent(file.canonicalPath, targetName)
                env[credentialFile.variable] = targetName as String

                return
            }
        }

        def searchedPaths = searchLocations.collect({
            "'${it}/credentials/${credentialFile.credentialsId}'"
        }).join(", ")

        throw new IllegalStateException("Unable to find credential ${credentialFile.credentialsId} in any of ${searchedPaths}.")
    }

    try {
        _increaseIndent code
    } finally {
        files.each { credentialFile ->
            env.remove(credentialFile.variable)
        }
    }
}

file = { config -> 
    return new CredentialFile(credentialsId: config.credentialsId,
                              variable: config.variable)
}
