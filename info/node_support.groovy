node = { name = null, code ->
    _runSectionWithId("node") { nodeId ->
        _log.message(_currentIndent((name ? "node: ${name}" : "node"), nodeId))

        _increaseIndent {
            code()
        }
    }
}
