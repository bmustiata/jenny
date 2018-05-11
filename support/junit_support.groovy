junit = { path ->
    _currentAgent.copyToLocal(
        path,
        "${_jennyConfig.junitFolder}"
    )

    try {
        _executeProcess.call(
            null,
            'xunit-viewer',
            "--results=${_jennyConfig.junitFolder}",
            "--output=${_jennyConfig.junitFolder}"
        )
        _log.message("> junit: ${_jennyConfig.junitFolder}")
    } catch (Exception e) {
        _log.message("> junit: failure generating junit. xunit-viewer missing?")
    }
}

