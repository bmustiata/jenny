parallel = { items ->
    items.each { parallelBranch, code ->
        if (parallelBranch == "failFast") {
            return;
        }

        println(_currentIndent("parallel: ${parallelBranch}", "parallel"))        
        _increaseIndent {
            code()
        }
    }
}
