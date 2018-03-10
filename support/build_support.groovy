build = { config ->
    if (config instanceof String) {
        config = ["job": config, "wait": true]
    }

    if (!config.containsKey("wait")) {
        config["wait"] = true
    }

    println("> =============================================")
    println("> build job: ${config.job}")
    println("> =============================================")
    if (config && config.containsKey("wait") && !config["wait"]) {
        println("> wait: nowait in external builds is not supported.")
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

    _jennyRun(parentId: (_jennyConfig.nestedIds ? "internal" : null),
              nestedIds: _jennyConfig.nestedIds,
              projectFolder: projectFolder)

    println("> build job ${config.job} ended.")
    println("> =============================================")
}
