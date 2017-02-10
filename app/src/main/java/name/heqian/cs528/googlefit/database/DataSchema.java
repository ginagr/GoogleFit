package name.heqian.cs528.googlefit.database;

public class DataSchema {
    public static final class TimeTable {
        public static final String NAME = "activities";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String ACTIVITY = "activity";
            public static final String STARTTIME = "startTime";
        }
    }
}

