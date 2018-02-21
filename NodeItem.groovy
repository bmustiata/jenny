class NodeItem {
    String baseId
    String leafId
    Map<String, Integer> idMap = [:]
    NodeItem parentNode
    static List<NodeItem> runningItems = []
    static boolean resumeSessionReached
    static boolean skipSessionReached

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

    boolean isInSkipedSection(_execute_skip_after_id) {
        if (!_execute_skip_after_id) {
            return false
        }

        if (NodeItem.skipSessionReached) {
            return true;
        }

        return false;
    }

    public String getFullId() {
        return (this.parentNode.baseId ? "${this.parentNode.fullId}." : "") + this.leafId
    }

    static NodeItem push(String baseId) {
        if (baseId == null) {
            def result = new NodeItem(baseId: null)
            NodeItem.runningItems.push(result)

            return result
        }

        def result = new NodeItem(baseId: baseId, 
                                  parentNode: NodeItem.runningItems.last())
        NodeItem.runningItems.push(result)
        result.leafId = result.computeLeafId()

        return result
    }

    static void pop() {
        NodeItem.runningItems.pop()
    }
}
