class CurrentBuild {
    def context

    Integer getNumber() {
        return 1
    }

    def result = null

    String getCurrentResult() {
        return "SUCCESS"
    }

    String displayName = "#1"
    String description = ""

    String getId() {
        "${this.number}"
    }

    long getTimeInMillis() {
        return 0
    }

    long getStartTimeInMillis() {
        return 0
    }

    long getDuration() {
        return 0
    }

    String getDurationString() {
        return "1m"
    }

    CurrentBuild getPreviousBuild() {
        return null
    }

    CurrentBuild getNextBuild() {
        return null
    }

    String getAbsoluteUrl() {
        return "https://github.com/bmustiata/jenny"
    }

    def getBuildVariables() {
        context.params
    }

    List getChangeSets() {
        return []
    }

    def getRawBuild() {
        return null
    }
}

currentBuild = new CurrentBuild(context: binding)
