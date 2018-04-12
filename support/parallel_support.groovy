parallel = { items ->
    _runSectionWithId("parallel") {
        items.each { parallelBranch, code ->
            _runSectionWithId("branch") {
                if (parallelBranch == "failFast") {
                    return;
                }

                _log.message "> --------------------------------------------------------------------"
                _log.message "> - parallel: ${parallelBranch}"
                _log.message "> --------------------------------------------------------------------"
                code()
            }
        }
    }
}
