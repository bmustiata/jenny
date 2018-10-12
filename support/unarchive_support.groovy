unarchive = { config ->
    if (!config.mapping) {
        throw new IllegalArgumentException(
            "You need to pass a mapping attribute in the form of " +
            "source/destination. For example: unarchive mapping: " +
            "['file1': 'renamed', 'some-folder/': '.']")
    }

    config.mapping.each { source, dest ->
        _currentAgent.copyToAgent(
            "${_jennyConfig.archiveFolder}/${source}",
            "${pwd()}/${dest}"
        )
    }
}
