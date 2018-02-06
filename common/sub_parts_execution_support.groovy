class NodeItem {
    String baseId
    String leafId
    Map<String, Integer> idMap = [:]
    NodeItem parentNode
    static List<NodeItem> runningItems = []
    static boolean resumeSessionReached

    /**
    * Generate an id for the given prefix. This will
    * take into account the ancestors.
    */
    String computeLeafId() {
        def key = ""
        if (this.baseId != null) {
            if (!this.parentNode.idMap.containsKey(this.baseId)) {
                this.parentNode.idMap[this.baseId] = 0
            }

            this.parentNode.idMap[this.baseId] = this.parentNode.idMap[this.baseId] + 1
            key = this.baseId.substring(0,1) + this.parentNode.idMap[this.baseId]
        }

        return key
    }

    boolean isInExecuteOnlySection(_execute_only_ids) {
        if (!_execute_only_ids) {
            return true
        }

        return _execute_only_ids.find {
            it.startsWith(this.fullId) || this.fullId.startsWith(it)
        } != null
    }

    boolean isInResumedSection(_execute_resume_from_id) {
        if (!_execute_resume_from_id || NodeItem.resumeSessionReached) {
            return true;
        }

        if (this.isInExecuteOnlySection([_execute_resume_from_id])) {
            return true;
        }

        return false
    }

    public String getFullId() {
        return (this.parentNode.baseId ? "${this.parentNode.fullId}." : "") + this.leafId
    }

    static NodeItem push(String baseId) {
        def result = new NodeItem(baseId: baseId, 
                                  parentNode: NodeItem.runningItems.last())
        NodeItem.runningItems.push(result)
        result.leafId = result.computeLeafId()

        return result
    }

    static void pop() {
        this.runningItems.pop()
    }
}

// add the root node.
NodeItem.runningItems.push(new NodeItem(baseId: null))

_runSectionWithId = { baseId, code ->
    try {
        def nodeItem = NodeItem.push(baseId)

        if (_execute_resume_from_id == nodeItem.fullId) {
            NodeItem.resumeSessionReached = true
        }
    
        if (_execute_skip_ids && _execute_skip_ids.contains(nodeItem.fullId)) {
            println("> jenny: Skipped ${baseId} ${nodeItem.fullId}")
            return
        }

        if (!nodeItem.isInExecuteOnlySection(_execute_only_ids)) {
            println("> jenny: Skipped ${baseId} ${nodeItem.fullId}")
            return
        }

        if (!nodeItem.isInResumedSection(_execute_resume_from_id)) {
            println("> jenny: Skipped ${baseId} ${nodeItem.fullId}")
            return
        }

        return code(nodeItem.fullId)
    } finally {
        NodeItem.pop()
    }
}
