stage = { name, code ->
    _runSectionWithId("stage") { stageId ->
        _log.message(_currentIndent("stage: ${name}", stageId))
        _increaseIndent code
    }
}
