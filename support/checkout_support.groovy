scm = "SCM"

def copyLargeDir =  { File dirFrom, File dirTo ->
    new AntBuilder().copy( todir:dirTo ) {
        fileset( dir:dirFrom )
    }
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
