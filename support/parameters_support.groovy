class Parameter {
    String name
    String defaultValue
    String description
}

class ParameterList {
    List<Parameter> parameters
}

string = { config -> 
    return new Parameter(name: config.name,
                         defaultValue: config.defaultValue,
                         description: config.description)
}

booleanParam = { config -> 
    return new Parameter(name: config.name,
                         defaultValue: config.defaultValue,
                         description: config.description)
}

parameters = { params ->  
    return new ParameterList(parameters: params)
}

properties = { props -> 
    props.each { prop ->
        if (prop instanceof ParameterList) {
            prop.parameters.each {
                _global[it.name] = it.defaultValue
            }

            return;
        }
    }

    return props
}
