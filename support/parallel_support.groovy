parallel = { items ->
    _runSectionWithId("parallel") {
        items.each { parallelBranch, code ->
            _runSectionWithId("branch") {
                if (parallelBranch == "failFast") {
                    return;
                }

                println "> --------------------------------------------------------------------"
                println "> - parallel: ${parallelBranch}"
                println "> --------------------------------------------------------------------"
                code()
            }
        }
    }
}
