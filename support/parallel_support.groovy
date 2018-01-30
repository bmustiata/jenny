parallel = { items ->
    items.each { parallelBranch, code ->
        println "--------------------------------------------------------------------"
        println "- parallel: ${parallelBranch}"
        println "--------------------------------------------------------------------"
        code()
    }
}

