parallel = { items ->
    _runSectionWithId("parallel") { parallelId ->
        _log.message(_currentIndent("parallel", parallelId))

        _increaseIndent {
            items.each { parallelBranch, code ->
                _runSectionWithId("branch") { branchId ->
                    if (parallelBranch == "failFast") {
                        return;
                    }

                    _log.message(_currentIndent("parallel branch: ${parallelBranch}", branchId))
                    _increaseIndent {
                        code()
                    }
                }
            }
        }
    }
}
