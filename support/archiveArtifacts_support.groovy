archiveArtifacts = { config ->
    _currentAgent.copyToLocal(
        config.artifacts,
        "${_jennyConfig.archiveFolder}"
    )
}

