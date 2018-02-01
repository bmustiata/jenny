node = { name = null, code ->
    println(_currentIndent((name ? "node: ${name}" : "node"), "node"))
    _increaseIndent {
        code()
    }
}
