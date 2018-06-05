ansiColor = { term, code ->
    _log.message(_currentIndent("ansiColor: ${term}"))
    def TERM = env['TERM']
    env['TERM'] = term

    try {
        _increaseIndent code
    } finally {
        env['TERM'] = TERM
    }
}

