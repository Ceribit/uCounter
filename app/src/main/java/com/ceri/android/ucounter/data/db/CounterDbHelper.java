package com.ceri.android.ucounter.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CounterDbHelper extends SQLiteOpenHelper {

    /** Define the database name and version*/
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "counter.db";

    /** Define default "Tag" for use in logging */
    public static final String TAG = CounterDbHelper.class.getSimpleName();

    /** Constructor taking in the context and the database's default version and name */
    public CounterDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    /** Initializes the Counter Table using CounterContract's CounterEntry Info */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_COUNTER_TABLE =
                "CREATE TABLE counter("
                + CounterContract.CounterEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CounterContract.CounterEntry.COLUMN_COUNTER_NAME + " TEXT NOT NULL,"
                + CounterContract.CounterEntry.COLUMN_COUNTER_COUNT + " INTEGER NOT NULL,"
                + CounterContract.CounterEntry.COLUMN_COUNTER_NEXT + " INTEGER"
                + ");";


        sqLiteDatabase.execSQL(SQL_CREATE_COUNTER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + CounterContract.CounterEntry.TABLE_NAME;
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
    }
}
