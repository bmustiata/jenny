class CredentialFile {
    String credentialsId
    String variable
}

withCredentials = { files, code ->
    println(_currentIndent("withCredentials"))
    _increaseIndent code
}

file = { config -> 
    return new CredentialFile(credentialsId: config.credentialsId,
                              variable: config.variable)
}
