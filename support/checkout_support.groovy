scm = "SCM"

def copyLargeDir =  { File dirFrom, File dirTo ->
    org.apache.commons.io.FileUtils.copyDirectory(dirFrom, dirTo)
}

checkout = { version ->
    if (version != "SCM") {
        throw new IllegalArgumentException("Only SCM checkout is supported.")
    }

    if (!new File(pwd()).exists()) {
        new File(pwd()).mkdirs()
    }
    
    copyLargeDir(_jennyConfig.projectFolder, new File(pwd()))
}
