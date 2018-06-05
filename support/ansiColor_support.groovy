ansiColor = { term, code ->
    _log.message("> ansiColor: ${term}")
    def TERM = env['TERM']
    env['TERM'] = term

    try {
        code()
    } finally {
        env['TERM'] = TERM
    }
}
