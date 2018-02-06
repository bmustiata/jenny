sh = { code ->
    println("> sh: ---------------------------------------")
    println(code)
    println("> -------------------------------------------")

    def process = new ProcessBuilder('bash', '-c', code)
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .redirectInput(ProcessBuilder.Redirect.INHERIT)

    process.environment().putAll(env)

    def exitCode = process.start().waitFor();

    if (exitCode != 0) {
        throw new IllegalStateException("Process execution failed, exit code: " + exitCode)
    }
}
