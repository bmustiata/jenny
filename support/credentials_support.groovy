class CredentialFile {
    String credentialsId
    String variable
}

withCredentials = { files, code ->
    files.each { credentialFile -> 
        env[credentialFile.variable] = credentialFile.credentialsId
    }

    code()
}

file = { config -> 
    return new CredentialFile(credentialsId: config.credentialsId,
                              variable: config.variable)
}
