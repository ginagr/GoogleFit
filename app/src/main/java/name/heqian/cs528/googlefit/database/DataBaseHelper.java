package name.heqian.cs528.googlefit.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import name.heqian.cs528.googlefit.database.DataSchema.TimeTable;

public class DataBaseHelper extends SQLiteOpenHelper{
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "activityBase.db";

    public DataBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TimeTable.NAME + "(" +
            "_id integer primary key autoincrement," +
                TimeTable.Cols.UUID + ", " +
                TimeTable.Cols.ACTIVITY + ", " +
                TimeTable.Cols.STARTTIME + ")"
            );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
