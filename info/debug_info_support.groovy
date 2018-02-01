
def _indentIndex = 0
def _idMap = [:]

_currentIndent = { message, id = null ->
    def indent = ""
    for (int i = 0; i < _indentIndex; i++) {
        indent += "  "
    }

    def key = ""
    if (id != null) {
        if (!_idMap.containsKey(id)) {
            _idMap[id] = 0
        }

        _idMap[id] = _idMap[id] + 1
        key = ' [id:' + id.substring(0,1) + _idMap[id] + '] '
    }

    return indent + message + key
}

_increaseIndent = { code -> 
    try {
        _indentIndex++
        return code()
    } finally {
        _indentIndex--
    }
}
