
def _indentIndex = 0

_currentIndent = { message, id = null ->
    def indent = ""
    for (int i = 0; i < _indentIndex; i++) {
        indent += "  "
    }

    def key = _generateId(id)

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
