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
    },

    /**
     * Log the exception, including the stack trace into the log
     * file only.
     */
    logException: { Exception e ->
        _parentLog.logException(e)
    }
]

