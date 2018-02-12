class CredentialFile {
    String credentialsId
    String variable
}

def searchLocations = [_jennyGlobalConfigFolder, ".jenny"]

withCredentials = { files, code ->
    files.each { credentialFile ->
        for (def searchLocation: searchLocations) {
            def file = new File(_projectFolder, "${searchLocation}/credentials/${credentialFile.credentialsId}")
            if (file.exists()) {
                env[credentialFile.variable] = file.canonicalPath
                return
            }
        }

        def searchedPaths = searchLocations.collect({
            "'${it}/credentials/${credentialFile.credentialsId}'"
        }).join(", ")

        throw new IllegalStateException("Unable to find credential ${credentialFile.credentialsId} in any of ${searchedPaths}.")
    }

    code()
}

file = { config -> 
    return new CredentialFile(credentialsId: config.credentialsId,
                              variable: config.variable)
}
