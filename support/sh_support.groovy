sh = { code ->
    def exitCode = new ProcessBuilder('bash', '-c', code)
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .redirectInput(ProcessBuilder.Redirect.INHERIT)
        .start()
        .waitFor();

    if (exitCode != 0) {
        throw new IllegalStateException("Process execution failed, exit code: " + exitCode)
    }
}
