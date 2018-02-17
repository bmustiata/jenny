build = { config ->
    if (config instanceof String) {
        config = ["job": config]
    }

    println("> =============================================")
    println("> build job: ${config.job}")
    println("> =============================================")
    if (config && config.containsKey("wait") && !config["wait"]) {
        println("> wait: nowait in external builds is not supported.")
    }

    _jennyRun(job: config.job)

    println("> build job ${config.job} ended.")
    println("> =============================================")
}
