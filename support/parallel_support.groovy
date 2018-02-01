parallel = { items ->    
    items.each { parallelBranch, code ->
        if (parallelBranch == "failFast") {
            return;
        }
        
        println "--------------------------------------------------------------------"
        println "- parallel: ${parallelBranch}"
        println "--------------------------------------------------------------------"
        code()
    }
}
