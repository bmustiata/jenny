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

    copyLargeDir(_projectFolder, new File(pwd()))
}
