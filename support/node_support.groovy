node = { name = null, code ->
    _runSectionWithId("node") {
        code()
    }
}
