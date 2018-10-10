unarchive = { config ->
    _currentAgent.copyToAgent(
        "${_jennyConfig.archiveFolder}/*",
        pwd()
    )
}

