stage = { name, code ->
    println(_currentIndent("stage: ${name}", "stage"))
    _increaseIndent code
}
