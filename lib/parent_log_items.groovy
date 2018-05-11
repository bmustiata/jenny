def logFileName = "${_workFolder}/jenny_${UUID.randomUUID() as String}.log"
writer = new FileWriter(logFileName)

_parentLog = [
    message:  { boolean onlyInLog = false, String message ->
        _parentLog.logMessage(String.format(
            "[<host>] %s",
            message))

        if (onlyInLog) {
            return;
        }

        _parentLog.printMessage(message)
    },

    /**
     * This message will be written only to the console.
     */
    printMessage:  { String message ->
        println(message)
    },

    /**
     * This message will be written only to the log
     * file, but not to the console.
     */
    logMessage: { String message ->
        writer.println(message)
    },

    close: {
        writer.flush()
        writer.close()
        if (!options.keepLog) {
            new File(logFileName).delete()
        }
    },

    /**
     * Log an exception, including the stack trace into the log
     * file.
     */
    logException: { Exception e ->
        def stackTraceWriter = new PrintWriter(writer)
        e.printStackTrace(stackTraceWriter)
        stackTraceWriter.flush()
    },

    // Publish the writer to log exceptions.
    writer: writer
]

