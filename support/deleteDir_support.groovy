deleteDir = { ->
    println("deleteDir: ${pwd()}")
    new File(pwd()).deleteDir()
}
