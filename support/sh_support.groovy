sh = { code ->
    println("> sh: ---------------------------------------")
    println(code)
    println("> -------------------------------------------")

    def process = new ProcessBuilder('bash', '-c', code)
        .directory(pwd())
        .inheritIO()
        
    process.environment().putAll(env)

    def exitCode = process.start().waitFor();

    if (exitCode != 0) {
        throw new IllegalStateException("Process execution failed, exit code: " + exitCode)
    }
}
