def buildParameters(buildConfig) {
    def result = [:]

    if (!buildConfig.parameters) {
        return result
    }

    if (!(buildConfig.parameters instanceof List)) {
        throw new IllegalStateException("The parameters to a build must be a list of maps.")
    }

    buildConfig.parameters.eachWithIndex { param, i ->
        if (!(param instanceof Map)) {
            throw new IllegalStateException("Illegal parameter at index ${i}, the parameters must be a list of maps.")
        }

        if (param.$class != 'StringParameterValue') {
            throw new IllegalStateException("Only `StringParameterValue` parameters are supported. Got a ${param.$class}.")
        }

        result[param.name] = param.value
    }

    return result
}

build = { config ->
    if (config instanceof String) {
        config = ["job": config, "wait": true]
    }

    if (!config.containsKey("wait")) {
        config["wait"] = true
    }

    _log.message(_currentIndent("build job: \"${config.job}\", wait: ${config.wait}"))

    if (config && config.containsKey("wait") && !config["wait"]) {
        _log.message("> wait: nowait in external builds is not supported.")
    }

    def jobLocation = config.job
    def projectFolder

    if (jobLocation in _jennyConfig.projects) {
        jobLocation = _jennyConfig.projects[jobLocation]
    }

    if (jobLocation[0] == '.') {
        projectFolder = new File(_jennyConfig.projectFolder, jobLocation)
                                .canonicalPath
    } else {
        projectFolder = new File(_jennyConfig.projectFolder.parentFile, jobLocation).canonicalPath
    }

    _increaseIndent {
        // When changing this don't forget to change the support as well
        _jennyRun(parentId: (_jennyConfig.nestedIds ? "internal" : null),
                workFolder: _jennyConfig.workFolder,
                nestedIds: _jennyConfig.nestedIds,
                projectFolder: projectFolder,
                libInfoAllowed: _jennyConfig.libInfoAllowed,
                params: buildParameters(config))
    }
}
