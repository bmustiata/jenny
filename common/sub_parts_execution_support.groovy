
_runSectionWithId = { baseId, code ->
    try {
        def nodeItem = NodeItem.push(baseId)

        if (_jennyConfig.execute.resumeFrom == nodeItem.fullId) {
            NodeItem.resumeSessionReached = true
        }

        if (_jennyConfig.execute.skipAfter == nodeItem.fullId) {
            NodeItem.skipSessionReached = true
        }

        if (_jennyConfig.execute.skip && _jennyConfig.execute.skip.contains(nodeItem.fullId)) {
            _log.message("> jenny: Skipped ${baseId} ${nodeItem.fullId}")
            return
        }

        if (!nodeItem.isInExecuteOnlySection(_jennyConfig.execute.only)) {
            _log.message("> jenny: Skipped ${baseId} ${nodeItem.fullId}")
            return
        }

        if (!nodeItem.isInResumedSection(_jennyConfig.execute.resumeFrom)) {
            _log.message("> jenny: Skipped ${baseId} ${nodeItem.fullId}")
            return
        }

        if (nodeItem.isInSkipedSection(_jennyConfig.execute.skipAfter)) {
            _log.message("> jenny: Skipped ${baseId} ${nodeItem.fullId}")
            return
        }

        return code(nodeItem.fullId)
    } finally {
        NodeItem.pop()
    }
}
