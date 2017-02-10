package name.heqian.cs528.googlefit;

public enum ActivitiesEnum {
    WALKING("Walking"),
    STILL("Still"),
    RUNNING("Running"),
    IN_VEHICLE("In Vehicle"),
    UNKNOWN("Unknown Activity");

    private final String text;

    /**
     * @param text
     */
    ActivitiesEnum(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
}
