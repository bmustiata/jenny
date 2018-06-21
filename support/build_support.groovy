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

    _log.message("> =============================================")
    _log.message("> build job: ${config.job}")
    _log.message("> =============================================")
    if (config && config.containsKey("wait") && !config["wait"]) {
        _log.message("> wait: nowait in external builds is not supported.")
    }

    def jobLocation = config.job
    def projectFolder

    if (jobLocation in _jennyConfig.projects) {
        jobLocation = _jennyConfig.projects[jobLocation]
    }

    // if we have a relative path starting from the
    // project folder we use that one
    if (jobLocation[0] == '.') {
        projectFolder = new File(_jennyConfig.projectFolder, jobLocation)
                                .canonicalPath
    } else {
        if ((jobLocation as File).absolute) {
            projectFolder = new File(jobLocation).canonicalPath
        } else {
            projectFolder = new File(_jennyConfig.projectFolder.parentFile, jobLocation).canonicalPath
        }
    }

    // When changing this don't forget to change the info support as well
    _jennyRun(parentId: (_jennyConfig.nestedIds ? "internal" : null),
              workFolder: _jennyConfig.workFolder,
              nestedIds: _jennyConfig.nestedIds,
              projectFolder: projectFolder,
              libInfoAllowed: _jennyConfig.libInfoAllowed,
              params: buildParameters(config))

    _log.message("> build job ${config.job} ended.")
    _log.message("> =============================================")
}
