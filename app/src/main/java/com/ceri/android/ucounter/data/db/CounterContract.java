package com.ceri.android.ucounter.data.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class CounterContract{

    /** Content Authority*/
    public static final String CONTENT_AUTHORITY = "com.ceri.android.ucounter";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_COUNTER = "counter";


    /** Prevents accidental initialization */
    private CounterContract(){}


    /** CounterEntry defines the Counter table columns and associated names */
    public static abstract class CounterEntry implements BaseColumns{
        // Content Value Types
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/text";
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COUNTER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COUNTER;

        // Content Uri Creation
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_COUNTER);

        // Table and Column Names
        public static final String TABLE_NAME = "counter";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_COUNTER_NAME = "name";
        public static final String COLUMN_COUNTER_COUNT = "count";
        public static final String COLUMN_COUNTER_NEXT = "next";
    }
}
