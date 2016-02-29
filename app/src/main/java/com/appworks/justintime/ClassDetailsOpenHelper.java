package com.appworks.justintime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by prashant on 29/2/16.
 */
public class ClassDetailsOpenHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "class.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME =   "class_table";
    public static final String ID = "id";
    public static final String CLASS_NAME = "class_name";
    public static final String DAY = "day";
    public static final String HOUR = "hour";
    public static final String MINUTE = "minute";
    public static final String AM_PM = "am_pm";
    public static final String TIME = "time";
    public static final String ADDRESS = "address";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    public static final String TABLE_CREATE_QUERY =
            "CREATE TABLE " + TABLE_NAME +
                    " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            CLASS_NAME + " TEXT, " +
                            DAY + " TEXT, " +
                            HOUR + " INTEGER, " +
                            MINUTE + " INTEGER, " +
                            AM_PM + " TEXT, " +
                            TIME + " TEXT, " +
                            ADDRESS + " TEXT, " +
                            LATITUDE + " REAL, " +
                            LONGITUDE + " REAL);";

    public ClassDetailsOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String className, String day, int hour, int minute, String am_pm,
                              String time, String address, double latitude, double longitude){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(CLASS_NAME, className);
        contentValues.put(DAY, day);
        contentValues.put(HOUR, hour);
        contentValues.put(MINUTE, minute);
        contentValues.put(AM_PM, am_pm);
        contentValues.put(TIME, time);
        contentValues.put(ADDRESS, address);
        contentValues.put(LATITUDE, latitude);
        contentValues.put(LONGITUDE, longitude);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE_NAME, null);

        return result;
    }
}
