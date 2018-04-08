pwd = { tmp = false ->
    if (tmp) {
        return "/tmp/${UUID.randomUUID() as String}"
    }

    return System.getProperty("user.dir")
}
