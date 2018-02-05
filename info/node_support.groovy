node = { name = null, code ->
    _runSectionWithId("node") { nodeId ->
        println(_currentIndent((name ? "node: ${name}" : "node"), nodeId))

        _increaseIndent {
            code()
        }
    }
}
