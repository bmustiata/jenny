_log = [
    message:  { boolean onlyInLog = false, String message ->
        _parentLog.logMessage(String.format(
            "[%s] %s",
            _currentAgent.nodeId,
            message
        ))

        if (onlyInLog) {
            return;
        }

        _parentLog.printMessage(message)
    }
]

