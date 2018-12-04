class CurrentBuild {
    def context

    Integer getNumber() {
        return Integer.valueOf(this.getId())
    }

    def result = null

    String getCurrentResult() {
        return "SUCCESS"
    }

    private String displayName = null
    String description = ""

    public String getDisplayName() {
        if (this.displayName == null) {
            return "#${this.context.env.BUILD_ID}"
        }

        return this.displayName
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName
    }

    String getId() {
        "${this.context.env.BUILD_ID}"
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
        this.context.params
    }

    List getChangeSets() {
        return []
    }

    def getRawBuild() {
        return null
    }
}

currentBuild = new CurrentBuild(context: binding)
