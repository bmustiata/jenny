class Parameter {
    private String name
    private Object defaultValue
    private String description

    def getArguments() {
        return this
    }
}


class ParameterList {
    List<Parameter> parameters
}

class PipelineTrigger {
}

class UpstreamTrigger extends PipelineTrigger {
    String threshold
    String upstreamProjects
}

class PipelineTriggers {
    List<PipelineTrigger> triggers
}

params = [:]

string = { config ->
    def valueConverter = { v -> "" + v }
    def defaultValue = params.containsKey(config.name) ?
                       valueConverter(params[config.name]) :
                       config.defaultValue

    def result =  new Parameter(name: config.name,
                         defaultValue: defaultValue,
                         description: config.description)

    return result
}

booleanParam = { config ->
    def valueConverter = { v -> Boolean.valueOf(v) }
    def defaultValue = params.containsKey(config.name) ?
                       valueConverter(params[config.name]) :
                       config.defaultValue

    def result =  new Parameter(name: config.name,
                         defaultValue: defaultValue,
                         description: config.description)

    return result
}

parameters = { parameters ->
    return new ParameterList(parameters: parameters)
}

pipelineTriggers = { triggers ->
    return new PipelineTriggers(triggers: triggers)
}

upstream = { config ->
    return new UpstreamTrigger(
        threshold: config.threshold,
        upstreamProjects: config.upstreamProjects
    )
}

properties = { props ->
    props.each { prop ->
        if (prop instanceof ParameterList) {
            prop.parameters.each {
                _global[it.name] = it.defaultValue
                params[it.name] = it.defaultValue
            }

            _log.message("> ==============================================")
            _log.message("> Parameters")
            _log.message("> --------------------------------------------")
            prop.parameters.each {
                _log.message("> ${it.name} = ${_global[it.name]}")
            }
            _log.message("> ==============================================")

            return;
        }

        if (prop instanceof PipelineTriggers) {
            _log.message("> ==============================================")
            _log.message("> Triggers")
            _log.message("> --------------------------------------------")
            prop.triggers.each {
                if (it instanceof UpstreamTrigger) {
                    _log.message("> * ${it.upstreamProjects}: ${it.threshold}")
                } else {
                    _log.message("> * ${it}")
                }
            }
            _log.message("> ==============================================")
        }
    }

    return props
}
