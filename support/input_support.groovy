input = { config ->
    def input = System.console().readLine config.message + " "
    if (input.toLowerCase().startsWith("n")) {
        throw new IllegalStateException("User selected no.")
    }
}

