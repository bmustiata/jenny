
if (!_jennyGlobal.containsKey("indentIndex")) {
    _jennyGlobal["indentIndex"] = 0
}

_currentIndent = { message, id = null ->
    def indent = ""
    for (int i = 0; i < _jennyGlobal["indentIndex"]; i++) {
        indent += "  "
    }

    def key = id ?: ""

    return indent + message + (key ? " [${key}]" : "")
}

_increaseIndent = { code -> 
    try {
        _jennyGlobal["indentIndex"] = _jennyGlobal["indentIndex"] + 1
        return code()
    } finally {
        _jennyGlobal["indentIndex"] = _jennyGlobal["indentIndex"] - 1
    }
}
