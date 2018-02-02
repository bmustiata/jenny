def _idMap = [:]

/**
 * Generate an id for the given prefix.
 */
_generateId = { id ->
    def key = ""
    if (id != null) {
        if (!_idMap.containsKey(id)) {
            _idMap[id] = 0
        }

        _idMap[id] = _idMap[id] + 1
        key = ' [id:' + id.substring(0,1) + _idMap[id] + '] '
    }

    return key
}
