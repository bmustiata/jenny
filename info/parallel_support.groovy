parallel = { items ->
    _runSectionWithId("parallel") { parallelId ->
        println(_currentIndent("parallel", parallelId))

        _increaseIndent {
            items.each { parallelBranch, code ->
                _runSectionWithId("branch") { branchId ->
                    if (parallelBranch == "failFast") {
                        return;
                    }

                    println(_currentIndent("parallel branch: ${parallelBranch}", branchId))
                    _increaseIndent {
                        code()
                    }
                }
            }
        }
    }
}
