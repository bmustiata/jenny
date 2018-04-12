_log = [
    message:  { boolean onlyInLog = false, String message ->
        _parentLog.message("[%s] %s".format(
            _currentAgent.nodeId,
            message
        ))

        if (onlyInLog) {
            return;
        }

        _parentLog.printMessage(message)
    }
]

