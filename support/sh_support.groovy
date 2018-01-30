sh = { code ->
    def sout = new StringBuilder(), serr = new StringBuilder()
    def proc = code.execute()

    proc.consumeProcessOutput(sout, serr)
    proc.waitForOrKill(1000)
    println "out> $sout err> $serr"
}

