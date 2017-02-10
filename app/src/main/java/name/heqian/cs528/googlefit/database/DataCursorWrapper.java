package name.heqian.cs528.googlefit.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;


import name.heqian.cs528.googlefit.ActInfo;
import name.heqian.cs528.googlefit.database.DataSchema.TimeTable;


public class DataCursorWrapper extends CursorWrapper {
    public DataCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public ActInfo getAct(){
        String uuidString = getString(getColumnIndex(TimeTable.Cols.UUID));
        String activity = getString(getColumnIndex(TimeTable.Cols.ACTIVITY));
        long startTime = getLong(getColumnIndex(TimeTable.Cols.STARTTIME));

        ActInfo act = new ActInfo(UUID.fromString(uuidString));
        act.setAct(activity);
        act.setStartTime(new Date(startTime));


        return act;

    }

}
