import org.yaml.snakeyaml.Yaml

Map.metaClass.addNested { Map rhs ->
    def lhs = delegate
    rhs.each { k, v ->
        if (lhs.containsKey(k)) {
            lhs[k].addNested(v)
        } else {
            lhs[k] = v
        }
    }   
    lhs
}

List.metaClass.addNested { List rhs ->
    def lhs = delegate
    rhs.each {
        lhs.add it
    }

    lhs
}

// -------------------------------------------------------------------
// Read the configuration from the home folder.
// -------------------------------------------------------------------
Yaml parser = new Yaml()
jennyConfig = [:]

loadConfigFile = { fileName ->
    if ((fileName as File).exists()) {
        println "> loading config file: ${fileName}"
        def loadedConfig = parser.load((fileName as File).text)
        jennyConfig.addNested(loadedConfig)
    }
}
