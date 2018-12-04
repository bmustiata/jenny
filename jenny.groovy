#!/usr/bin/env groovy

import org.codehaus.groovy.control.CompilerConfiguration

// find where jenny is installed
File scriptFile = new File(getClass().protectionDomain.codeSource.location.path)
def classLoader = new GroovyClassLoader(getClass().getClassLoader())
Class NodeItem = classLoader.parseClass(new File(scriptFile.parent, "NodeItem.groovy"));

// -------------------------------------------------------------------
// Parse the CLI options.
// -------------------------------------------------------------------
def cli = new CliBuilder(usage: "jenny [options]")
cli.f(longOpt: "file", args: 1, argName: "file", "Path to Jenkinsfile.")
cli.l(longOpt: "lib", args: -2, argName: "lib", "Path to the library to load.")
cli.w(longOpt: "workFolder", args: 1, "Work folder (defaults to ${System.getProperty("java.io.tmpdir")})")
cli.archiveFolder("Folder to store the outputs of archiveArtifacts")
cli.junitFolder("Folder to store the outputs of junit")
cli.keepLog("Keep the generated log file after finishing.")
cli.p(longOpt: "param", args: -2, argName: "nam=value", valueSeparator:'=', "Parameter to override.")
cli.e(longOpt: "env", args: -2, argName: "nam=value", valueSeparator:'=', "Environment variable to override.")
cli.s(longOpt: "skip", args: -2, argName: "id", "stage/parallel/node blocks to skip by ID.")
cli.o(longOpt: "only", args: -2, argName: "id", "stage/parallel/node blocks to run by ID (includes ancestors).")
cli.rf(longOpt: "resumeFrom", args: 1, argName: "id", "stage/parallel/node blocks to continue from.")
cli.sa(longOpt: "skipAfter", args: 1, argName: "id", "stage/parallel/node blocks to skiped when reached (inclusive).")
cli.ni(longOpt: "nestedIds", "Generate unique ids for the whole run, including nested runs. Execution restrictions from config files will be ignored.")
cli.noLogo(longOpt: "noLogo", "Don't show the logo.")
cli.i(longOpt: "info", "Show information on the current jenkinsfile, including ids.")
cli.v(longOpt: "verbose", "Show more messages")
cli.h(longOpt: "help", "Show this message.")

options = cli.parse(args)

if (options.h) {
    cli.usage();
    return;
}

_workFolder = options.workFolder ?: System.getProperty("java.io.tmpdir")

// -------------------------------------------------------------------
// Load the core libraries (config, lib loading)
// -------------------------------------------------------------------
new File(scriptFile.parent, "lib").listFiles().each {
    evaluate(it)
}

jennyGlobalConfigFolder = "${System.getenv('HOME')}/.jenny"
jennyGlobal = [:]

abstract class JennyScript extends Script {
    def methodMissing(String name, args) {
        _log.message(_currentIndent("${name} (MOCK)"))

        if (!args) {
            return null
        }

        def lastArgument = args.last()

        if (!lastArgument || !lastArgument.metaClass) {
            return null
        }

        if (!lastArgument.metaClass.respondsTo(lastArgument, "call")) {
            return null
        }

        _increaseIndent args.last()

        return null
    }
}

/**
 * Execute a build for a jenkinsfile. This uses also the parent context
 * when calling nested builds run.
 */
jennyRun = { runConfig ->

    // -------------------------------------------------------------------
    // Read the configuration for the current project.
    // -------------------------------------------------------------------
    def jennyConfig = [
        "libs":[],
        "params":[:],
        "env": [
            "BRANCH_NAME": "master",
            "BUILD_ID": "1",
            "BUILD_NUMBER": "1"
        ],
        "execute":[:],
        "projects":[:],
        "workFolder": runConfig["workFolder"],
        "nestedIds": runConfig["nestedIds"],
        "verbose": runConfig["verbose"],
        "info": options.info,
        "libInfoAllowed": runConfig["libInfoAllowed"]
    ]

    def projectFolder = new File(new File(runConfig.projectFolder ?: ".").canonicalPath)

    if (jennyConfig.verbose) {
        _parentLog.message("> project folder: ${projectFolder.canonicalPath}")
    }

    if (!projectFolder.exists()) {
        throw new IllegalArgumentException("Unable to run build since ${projectFolder} doesn't exists.")
    }

    loadConfigFile(jennyConfig, "${jennyGlobalConfigFolder}/config")
    loadConfigFile(jennyConfig, new File(projectFolder, ".jenny/config").canonicalPath)
    jennyConfig["params"].addNested(runConfig.params)
    jennyConfig["env"].addNested(runConfig.env)

    if (runConfig.topProject || runConfig.nestedIds) {
        loadCommandLineOptions(jennyConfig, options)
    }

    // -------------------------------------------------------------------
    // Start the execution.
    // -------------------------------------------------------------------
    if (runConfig.topProject) {
        def jennyLogo = """\
        >    _
        >   (_) ___ _ __  _ __  _   _
        >   | |/ _ \\ '_ \\| '_ \\| | | |
        >   | |  __/ | | | | | | |_| |
        >  _/ |\\___|_| |_|_| |_|\\__, |
        > |__/                  |___/
        > console jenkins runner
        >
        """.stripIndent()

        if (jennyConfig.noLogo) {
            _parentLog.logMessage(jennyLogo)
        } else {
            _parentLog.message(jennyLogo)
        }
    }

    // -------------------------------------------------------------------
    // Prepare the execution context.
    // -------------------------------------------------------------------
    def binding = new Binding()
    binding._global = binding
    binding._jennyConfig = jennyConfig
    binding._jennyRun = jennyRun
    binding._jennyGlobal = jennyGlobal
    binding._parentLog = _parentLog

    binding.NodeItem = NodeItem
    binding._runInFolder = runInFolder

    def compilerConfiguration = new CompilerConfiguration()
    compilerConfiguration.setScriptBaseClass('JennyScript')

    def shell = new GroovyShell(classLoader, binding, compilerConfiguration)

    // needed for credentials
    jennyConfig.jennyGlobalConfigFolder = jennyGlobalConfigFolder
    // needed for credentials/checkout
    jennyConfig.projectFolder = projectFolder

    // -------------------------------------------------------------------
    // Load the default suport functions
    // -------------------------------------------------------------------
    // jenny can run in two modes:
    // 1. is to actually execute things,
    // 2. is to just provide information to the user
    //    on what will be executed, and provide the IDs

    // load all the support files.
    new File(scriptFile.parent, "common").listFiles().each {
        shell.evaluate(it)
    }

    new File(scriptFile.parent, options.info ? "info" : "support").listFiles().each {
        shell.evaluate(it)
    }

    // -------------------------------------------------------------------
    // Create the workspace
    // -------------------------------------------------------------------
    // workspaceFolder is needed for pwd
    def config1 = binding._prepareWorkspace.call()
    jennyConfig.archiveFolder = config1.archiveFolder
    jennyConfig.junitFolder = config1.junitFolder
    jennyConfig.workspaceFolder = config1.workspaceFolder

    // -------------------------------------------------------------------
    // Load the external libraries
    // -------------------------------------------------------------------
    if (options.info) {
        loadInfoLibraries(shell, binding)
    } else {
        loadLibraries(shell, binding)
    }

    // -------------------------------------------------------------------
    // Override parameters
    // -------------------------------------------------------------------
    if (jennyConfig.params) {
        binding.params.addNested(jennyConfig.params)
    }

    // -------------------------------------------------------------------
    // Override environment
    // -------------------------------------------------------------------
    if (jennyConfig.env) {
        binding.env.addNested(jennyConfig.env)
    }

    // this will run the given jenkinsfile
    def jenkinsFile = new File(jennyConfig.projectFolder,
                               runConfig.jenkinsFile ?: "Jenkinsfile")

    if (!jenkinsFile.exists()) {
        throw new IllegalArgumentException("File ${jenkinsFile.canonicalPath} does not exists.")
    }

    binding.NodeItem.push(runConfig.parentId)
    // add the root node.
    binding._runInFolder.call(jennyConfig.workspaceFolder, {
        shell.evaluate(jenkinsFile)
    }, ignoreMissing=jennyConfig.info)
}

// -------------------------------------------------------------------
// Detect the top parent where the Jenkinsfile is.
// -------------------------------------------------------------------
File projectFolder = new File(new File(".").canonicalPath)
String jenkinsFileName = (options.file ?: "Jenkinsfile")

while (! new File(projectFolder, jenkinsFileName).exists()) {
    if (projectFolder.toPath().nameCount == 0) {
        throw new IllegalArgumentException("Unable to find any ${jenkinsFileName} in any of the parents of ${new File(".").canonicalPath}")
    }
    projectFolder = projectFolder.parentFile
}

try {
    jennyRun([
        parentId: null,
        projectFolder: projectFolder.canonicalPath,
        workFolder: _workFolder,
        jenkinsFile: (options.file ?: "Jenkinsfile"),
        topProject: true,
        nestedIds: options.nestedIds,
        verbose: options.verbose,
        libInfoAllowed: []
    ])
} catch (Exception e) {
    _parentLog.message(e.getMessage())
    _parentLog.logException(e)

    System.exit(1)
} finally {
    _parentLog.close()
}

