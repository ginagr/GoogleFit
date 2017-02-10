package name.heqian.cs528.googlefit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import name.heqian.cs528.googlefit.database.DataBaseHelper;
import name.heqian.cs528.googlefit.database.DataCursorWrapper;
import name.heqian.cs528.googlefit.database.DataSchema;

class ActLab {
    private static ActLab sActLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static ActLab get(Context context) {
        if (sActLab == null) {
            sActLab = new ActLab(context);
        }
        return sActLab;
    }

    private ActLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new DataBaseHelper(mContext)
                .getWritableDatabase();
    }


    public void addAct(ActInfo c) {
        ContentValues values = getContentValues(c);

        mDatabase.insert(DataSchema.TimeTable.NAME, null, values);
    }

    public List<ActInfo> getAct() throws ParseException {
        List<ActInfo> crimes = new ArrayList<>();

        DataCursorWrapper cursor = queryAct(null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            crimes.add(cursor.getAct());
            cursor.moveToNext();
        }
        cursor.close();

        return crimes;
    }

    public ActInfo getAct(UUID id) {
        DataCursorWrapper cursor = queryAct(
                DataSchema.TimeTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getAct();
        } finally {
            cursor.close();
        }
    }


    public void updateAct(ActInfo actI) {
        String uuidString = actI.getId().toString();
        ContentValues values = getContentValues(actI);

        mDatabase.update(DataSchema.TimeTable.NAME, values,
                DataSchema.TimeTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    private static ContentValues getContentValues(ActInfo actI) {
        ContentValues values = new ContentValues();
        values.put(DataSchema.TimeTable.Cols.UUID, actI.getId().toString());
        values.put(DataSchema.TimeTable.Cols.ACTIVITY, actI.getAct());
        values.put(DataSchema.TimeTable.Cols.STARTTIME, actI.getStartTime().getTime());

        return values;
    }

    private DataCursorWrapper queryAct(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DataSchema.TimeTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy\

        );

        return new DataCursorWrapper(cursor);
    }
}
