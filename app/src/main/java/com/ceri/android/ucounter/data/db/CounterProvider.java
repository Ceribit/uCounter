package com.ceri.android.ucounter.data.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.ceri.android.ucounter.data.db.CounterContract.CounterEntry;
public class CounterProvider extends ContentProvider {


    // ********************
    // Initialization
    // ********************
    /** Global Database Variables */
    CounterDbHelper mCounterDbHelper;
    SQLiteDatabase mDatabase;

    /** Tag for the log messages */
    public static final String TAG = CounterProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the counter table */
    private static final int COUNTER = 100;

    /** URI matcher code for the content URI for a single counter in the counter table */
    private static final int COUNTER_ID = 101;

    /** onCreate initializes the DBHelper and SQLite Database*/
    @Override
    public boolean onCreate() {
        mCounterDbHelper = new CounterDbHelper(getContext());
        return true;
    }


    // ********************
    // URI
    // ********************
    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    /** Static Initializer : Calls addUri to add content URI patterns to the provider*/
    static {
        sUriMatcher.addURI(CounterContract.CONTENT_AUTHORITY,
                CounterContract.PATH_COUNTER, COUNTER);
        sUriMatcher.addURI(CounterContract.CONTENT_AUTHORITY,
                CounterContract.PATH_COUNTER + "/#", COUNTER_ID);
    }


    // ********************
    // Querying
    // ********************

    /**
     *  Queries the database according to providedparameters
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        mDatabase = mCounterDbHelper.getReadableDatabase();
        mDatabase.setLockingEnabled(false);
        Cursor cursor;

        // Match provided URI to known codes
        int match = sUriMatcher.match(uri);
        switch (match){
            case COUNTER:
                cursor = mDatabase.query(CounterEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case COUNTER_ID:
                long id = ContentUris.parseId(uri);
                //selection = CounterEntry._ID + "=?";
                //selectionArgs = new String[]{String.valueOf(id)};
                cursor = mDatabase.query(CounterEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }

        // Set Notification URI
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    // ********************
    // Insert
    // ********************

    /**
     * Inserts data into database based on its Uri
     * @param uri Address of the location
     * @param contentValues The set of values to insert into the database
     * @return uri Returns uri of the inserted counter
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        // Redirected to function based off matched Uri
        int match = sUriMatcher.match(uri);
        switch (match){
            case COUNTER:
                return insertCounter(uri, contentValues);
            case COUNTER_ID:
                Log.e(TAG, "Counter_ID switch statement ran.");
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
    }

    /**
     *  Inserts data into the database. Also checks the quality of the information being sent
     * @param uri Address of the location
     * @param contentValues The set of values to insert into the database
     * @return uri Returns uri of the inserted counter
     */
    public Uri insertCounter(Uri uri, ContentValues contentValues){

        // Retrieve information sent by the content value
        String name = contentValues.getAsString(CounterContract.CounterEntry.COLUMN_COUNTER_NAME);
        int count = contentValues.getAsInteger(CounterContract.CounterEntry.COLUMN_COUNTER_COUNT);

        // Check information sent by the content value
        if(name == null){
            throw new IllegalArgumentException("No name specified.");
        }

        // Retrieve readable database
        mDatabase = mCounterDbHelper.getReadableDatabase();
        // Insert counter into the database
        long id = mDatabase.insert(CounterContract.CounterEntry.TABLE_NAME, null, contentValues);

        if(id == -1){
            Log.e(TAG, "Failed to insert counter into the database");
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }


    // ********************
    // Update
    // ********************

    /**
     * Provides the associated update function with the Uri's matched ID
     * @param uri Address of the location
     * @param contentValues The set of values to insert into the database
     * @param selection Defines the types of attributes as to query with to find the values
     * @param selectionArgs Specifies the values of the attributes being queried
     * @return The number of rows updated
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                        String[] selectionArgs) {

        // Redirected to function based off matched Uri
        int match = sUriMatcher.match(uri);
        switch (match) {
            case COUNTER:
                return updateCounter(uri, contentValues, selection, selectionArgs);
            case COUNTER_ID:
                return updateCounter(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
    }

    /**
     * Updates the values at a given uri with the information provided in contentValues
     * @param uri Address of the location
     * @param contentValues The set of values to insert into the database
     * @param selection Defines the types of attributes as to query with to find the values
     * @param selectionArgs Specifies the values of the attributes being queried
     * @return The number of rows updated
     */
    public int updateCounter(Uri uri, ContentValues contentValues, String selection,
                            String[] selectionArgs){

        // Get writable database
        mDatabase = mCounterDbHelper.getWritableDatabase();

        // Retrieve information sent by the content value

        //String name = contentValues.getAsString(CounterContract.CounterEntry.COLUMN_COUNTER_NAME);
        //int count = contentValues.getAsInteger(CounterContract.CounterEntry.COLUMN_COUNTER_COUNT);

        // Retrieve database
        SQLiteDatabase db = mCounterDbHelper.getWritableDatabase();

        int numRowsUpdated = db.update(CounterContract.CounterEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);

        return numRowsUpdated;
    }


    // ********************
    // Delete
    // ********************

    /**
     * Provides the associated delete function with the Uri's matched ID
     * @param uri Address of the location
     * @param selection Defines the types of attributes as to query with to find the values
     * @param selectionArgs Specifies the values of the attributes being queried
     * @return The number of rows updated
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        deleteCounter(uri, selection, selectionArgs);
        return 0;
        }

    /**
     * Delete the values at a given Uri with the information provided in contentValues
     * @param uri Address of the location
     * @param selection Defines the types of attributes as to query with to find the values
     * @param selectionArgs Specifies the values of the attributes being queried
     * @return The number of rows updated
     */
    public int deleteCounter(Uri uri, String selection, String[] selectionArgs) {
        mCounterDbHelper = new CounterDbHelper(getContext());
        mDatabase = mCounterDbHelper.getReadableDatabase();

        mDatabase.delete(CounterContract.CounterEntry.TABLE_NAME, null, null);

        return 0;
    }


    // ********************
    // Type
    // ********************

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case COUNTER_ID:
                return CounterContract.CounterEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    // ********************
    // Utility Functions
    // ********************

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        Bundle bundle = new Bundle();
        if(method.equals("getRowCount")){
            int numRows = getRowCount();
            bundle.putInt("numRows", numRows);
        }
        return bundle;
    }

    public int getRowCount() {
        mDatabase = mCounterDbHelper.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(mDatabase, CounterEntry.TABLE_NAME);
    }
}
