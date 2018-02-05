
def _indentIndex = 0

_currentIndent = { message, id = null ->
    def indent = ""
    for (int i = 0; i < _indentIndex; i++) {
        indent += "  "
    }

    def key = id ?: ""

    return indent + message + (key ? " [${key}]" : "")
}

_increaseIndent = { code -> 
    try {
        _indentIndex++
        return code()
    } finally {
        _indentIndex--
    }
}
