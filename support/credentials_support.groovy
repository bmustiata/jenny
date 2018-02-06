class CredentialFile {
    String credentialsId
    String variable
}

withCredentials = { files, code ->
    files.each { credentialFile -> 
        def file = new File(_projectFolder, ".jenny/credentials/${credentialFile.credentialsId}")
        env[credentialFile.variable] = file.canonicalPath
    }

    code()
}

file = { config -> 
    return new CredentialFile(credentialsId: config.credentialsId,
                              variable: config.variable)
}
