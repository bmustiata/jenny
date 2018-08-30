stash = { config ->
    _log.message("> stash: ${config.name} -> ${config.includes}")
    _mkdirp("${_jennyConfig.archiveFolder}/${config.name}/" as File)
    _currentAgent.copyToLocal(
        config.includes,
        "${_jennyConfig.archiveFolder}/${config.name}/"
    )
}

unstash = { name ->
    _log.message("> unstash: ${name}")
    _currentAgent.copyToAgent(
        "${_jennyConfig.archiveFolder}/${name}/*",
        pwd()
    )
}
