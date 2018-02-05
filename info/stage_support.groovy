stage = { name, code ->
    _runSectionWithId("stage") { stageId ->
        println(_currentIndent("stage: ${name}", stageId))
        _increaseIndent code
    }
}
