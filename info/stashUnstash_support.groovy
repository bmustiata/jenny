stash = { config ->
    _log.message(_currentIndent("stash: ${config.name} -> ${config.includes}"))
}

unstash = { name ->
    _log.message(_currentIndent("unstash: ${name}"))
}
