package com.ceri.android.ucounter.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ceri.android.ucounter.data.db.CounterContract;
import com.ceri.android.ucounter.data.db.CounterContract.CounterEntry;
import com.ceri.android.ucounter.ui.CounterInfo;

import java.util.ArrayList;

public class CounterDataController {

    // Context of the view, needed to access the ContentResolver
    private Context mContext;

    // Links to ContentProvider to access database
    private ContentResolver mContentResolver;

    // Is used to store the last value in the table (may not be needed)
    private SharedPreferences mPreferences;

    // Holds the location of the SharedPrefFile
    private String sharedPrefFile = "com.ceri.android.counterprefs";

    // Debug tag
    private static String TAG = CounterDataController.class.getSimpleName();


    public CounterDataController(){
    }


    // Sets context
    public void setContext(Object context){
        mContext = (Context) context;
        mContentResolver = mContext.getContentResolver();
    }

    // Changes the recorded id of the tail
    private void setTailIdPreference(int id){
        mPreferences = mContext.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putInt("tailId", id);
        preferencesEditor.apply();
    }


    /****************************
     *
     *
     * Insert
     *
     *
     ****************************/


    // TODO: Create a Linked List that will keep the data sorted
    public boolean insertCounterAtEnd(String name, int initialValue, int currentPosition) {
        return true;
    }


    /**
     * Appends a new counter variable such that the last positioned one points to it
     * @param name The name of the new counter entry
     * @param initialValue The initial value of the new counter entry
     * @return Returns a boolean variable which states whether the insert was successful or not
     */
    public boolean appendCounter(String name, int initialValue){
        // Update mPreferences
        mPreferences = mContext.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE);

        // Creates new counter entry
        ContentValues contentValues = new ContentValues();
        contentValues.put(CounterContract.CounterEntry.COLUMN_COUNTER_NAME, name);
        contentValues.put(CounterContract.CounterEntry.COLUMN_COUNTER_COUNT, initialValue);
        contentValues.put(CounterContract.CounterEntry.COLUMN_COUNTER_NEXT, -1);

        // Gets last recorded tailId
        int tailId = mPreferences.getInt("tailId", -1);
        Log.e(TAG, "AppendCounter tail is " + tailId );
        int rowCount =
                (mContentResolver.call(CounterEntry.CONTENT_URI,"getRowCount",
                        null, null).getInt("numRows"));
        if (rowCount == 0) { // If table is empty, add a head
            Log.e(TAG, "AppendCounter: No head found.");
            insertHead();
        } else{
            Log.e(TAG, "AppendCounter: Row count = " + rowCount);
        }

        if ( tailId == -1) { // Not yet initialized
            // TODO: Check if SharedPreferences was deleted
            Log.e(this.getClass().getSimpleName(), "Tail Id was -1");
            appendToHead(contentValues);
        } else{ // Only one value
            Log.e(this.getClass().getSimpleName(), "Tail Id was not -1");
            append(contentValues, tailId);
        }
        return true;
    }


    // If the database is empty, this creates a new head
    private void insertHead(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CounterEntry._ID, 1);
        contentValues.put(CounterEntry.COLUMN_COUNTER_NAME, "DEFAULT");
        contentValues.put(CounterEntry.COLUMN_COUNTER_COUNT, "1");
        contentValues.put(CounterEntry.COLUMN_COUNTER_NEXT, "-1");
        mContentResolver.insert(CounterEntry.CONTENT_URI, contentValues);
        Log.e("InsertHead", "Head inserted");
    }



    // If the head is the only value, this updates it to point towards a new counter
    private void appendToHead(ContentValues contentValue){
        Log.e(TAG, "AppendToHead Called.");
        // Create a new value in the table using the given contentValue
        Uri newCounterUri = mContentResolver.insert(CounterContract.CounterEntry.CONTENT_URI, contentValue);
        long newCounterId = ContentUris.parseId(newCounterUri);

        // Set value so that the head will now point to the first variable
        ContentValues nextValue = new ContentValues();
        nextValue.put(CounterContract.CounterEntry.COLUMN_COUNTER_NEXT, newCounterId);

        // Query the head of the table
        String selection = CounterContract.CounterEntry._ID + "=?";
        String[] selectionArgs = {"1"};

        // Update the table to point to the new value
        mContentResolver.update(
                CounterContract.CounterEntry.CONTENT_URI,
                nextValue,
                selection,
                selectionArgs
        );
        // Edit SharedPreferences to have it's tail point toward the new counter
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putInt("tailId", (int) newCounterId);
        preferencesEditor.apply();
    }



    // Updates the tail of the database to point towards the newest element
    private void append(ContentValues contentValue, int lastId){
        // Create a new value in the table using the given contentValue
        Uri newCounterUri = mContentResolver.insert(CounterEntry.CONTENT_URI, contentValue);
        long newCounterId = ContentUris.parseId(newCounterUri);

        // Set value so that the head will now point to the first variable
        ContentValues nextValue = new ContentValues();
        nextValue.put(CounterContract.CounterEntry.COLUMN_COUNTER_NEXT, newCounterId);

        // Create query arguments
        Uri tailUri = Uri.withAppendedPath(CounterEntry.CONTENT_URI, String.valueOf(lastId));
        String selection = CounterEntry._ID + "=?";
        String[] selectionArgs = {String.valueOf(lastId)};

        // Update the table's tail to point to the new value
        mContentResolver.update(
                tailUri,
                nextValue,
                selection,
                selectionArgs
        );

        // Edit SharedPreferences to have it's tail point toward the new counter
        setTailIdPreference((int)newCounterId);
    }



    /*** ************************
     *
     *
     * Data Retrieval
     *
     *
     ****************************/

    /**
     * Returns an array list of each id, where the integer's index is an ID and its placement is its
     * position in the PageAdapter
     * */
    public static ArrayList<Integer> getPositionList(Object view){
        ContentResolver content = ((Context) view).getContentResolver();

        // Initialize query variables
        String[] projection = {
                CounterEntry._ID,
                CounterEntry.COLUMN_COUNTER_NEXT
        };
        String selection = CounterEntry._ID + "=?";
        String[] selectionArgs = {"1"};
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(1);

        int currentNode = 1;
        int newTail = -1;

        // Keep moving through the linked list until you reach the end
        while(currentNode != -1){
            // Given a node's id, it finds the next value of the node
            Cursor cursor = content.query(
                    CounterEntry.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null);
            cursor.moveToPosition(0);

            // Sets the current node to be the next's
            try{
                currentNode = cursor.getInt(cursor.getColumnIndex(CounterEntry.COLUMN_COUNTER_NEXT));
                selectionArgs[0] = String.valueOf(currentNode);
                // Add the found node to the array list
                if(currentNode!=-1) {
                    arrayList.add(currentNode);
                }
            } catch (CursorIndexOutOfBoundsException e){
                //Log.e(TAG, "Cursor was out of bounds");
                cursor.close();
                return arrayList;
            }
            cursor.close();
        }
        return arrayList;
    }


    /**
     *  Updates a given id with a new value and/or name
     * @param id New id of the counter
     * @param name New name of the counter
     * @param oldValue New
     * @param step The
     * @return Returns whether the counter was successfully updated
     */
    public boolean updateCounter(int id, @Nullable String name, @Nullable Integer oldValue, @Nullable int step){
        // Track the number of rows updated (should be either zero or one)
        int rowsUpdated;

        ContentValues contentValue = new ContentValues();
        // Updates value if it exists
        if(oldValue != null) {
            contentValue.put(CounterContract.CounterEntry.COLUMN_COUNTER_COUNT, oldValue + step);
        }

        // Updates name if it exists
        if(name != null){
            Log.e(TAG, "Name was not equal to null and added: " + name);
            contentValue.put(CounterEntry.COLUMN_COUNTER_NAME, name);
        }

        // Updates the value
        String selection = CounterContract.CounterEntry._ID + " =?";
        String[] selectionArgs = {String.valueOf(id)};
        rowsUpdated = mContentResolver.update(
                CounterContract.CounterEntry.CONTENT_URI,
                contentValue,
                selection,
                selectionArgs
        );
        return (rowsUpdated > 0);
    }



    // Returns a CounterInfo object (id, name, value, next) given an id
    public CounterInfo getCounterData(int id){
        CounterInfo counterInfo = new CounterInfo();

        String selection = CounterEntry._ID + "=?";
        String selectionArgs[] = {String.valueOf(id)};
        mContentResolver = mContext.getContentResolver();

        Cursor cursor = mContentResolver.query(
                CounterEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null
        );

        if(cursor != null) {
            cursor.moveToFirst();
            counterInfo.setId(cursor.getInt(cursor.getColumnIndex(CounterEntry._ID)));
            counterInfo.setName(cursor.getString(cursor.getColumnIndex(CounterEntry.COLUMN_COUNTER_NAME)));
            counterInfo.setValue(cursor.getInt(cursor.getColumnIndex(CounterEntry.COLUMN_COUNTER_COUNT)));
            counterInfo.setNext(cursor.getInt(cursor.getColumnIndex(CounterEntry.COLUMN_COUNTER_NEXT)));
            cursor.close();
        }
        return counterInfo;
    }

    /****************************
     *
     *
     * Delete
     *
     *
     ****************************/

    // Replaces the previous counter's "NEXT" and replaces it with the current counter's "NEXT"
    // This then deletes the current counter from the database
    public boolean deleteCounter(int id){
        // Creates query arguments
        String deleteSelection = CounterEntry._ID + "=?";
        String selectionArgs[] = {String.valueOf(id)};

        // Changes the id of the counter pointing to this one
        boolean isSuccessful = replacePrevId(id);
        Log.e(TAG, "Attempting to delete" + deleteSelection + " with id=" + String.valueOf(id) );
        // Delete the requested value
        long rowsDeleted = mContentResolver.delete(
                CounterEntry.CONTENT_URI,
                deleteSelection,
                selectionArgs
        );

        // Checks if the data was deleted
        if (rowsDeleted != 0){
            isSuccessful = false;

        }
        Log.e(TAG, "DeleteCounter : Rows deleted = " + rowsDeleted);
        return rowsDeleted>0;
    }



    // Given a counter's id, this replaces the pointer of the counter that points to it with the
    // given counter's id
    private boolean replacePrevId(int id){
        Log.e(TAG, "replacePrevId: Id=" + id);
        // Get Content Resolver
        mContentResolver = mContext.getContentResolver();
        String idSelection = CounterEntry._ID + "=?";
        String updateSelection = CounterEntry.COLUMN_COUNTER_NEXT + "=?";
        String selectionArgs[] = {String.valueOf(id)};

        // Query to find what the deleted value pointed to
        Cursor currentNode = mContentResolver.query(
                CounterEntry.CONTENT_URI,
                null,
                idSelection,
                selectionArgs,
                null
        );

        // Update the head of the delete value to point towards it's original tail
        if(currentNode != null) {
            // Move cursor to the beginning
            currentNode.moveToFirst();

            // Get what the current value points to
            int currentNodeNext = currentNode.getInt(
                    currentNode.getColumnIndex(CounterEntry.COLUMN_COUNTER_NEXT));
            currentNode.close();
            Log.e(TAG, "replacePrevId: currentNodeNext=" + currentNodeNext);

            // Update previous node to point to CurrentNode's Next
            ContentValues contentValues = new ContentValues();
            contentValues.put(CounterEntry.COLUMN_COUNTER_NEXT, currentNodeNext);
            long rowsUpdated = mContentResolver.update(
                    CounterEntry.CONTENT_URI,
                    contentValues,
                    updateSelection,
                    new String[]{String.valueOf(id)}
            );

            // If delete node is at the end, update SharedPreference's tailId
            if(currentNodeNext == -1){
                // Find past node
                Cursor prevNode = mContentResolver.query(
                        CounterEntry.CONTENT_URI,
                        null,
                        updateSelection,
                        new String[]{String.valueOf(-1)},
                        null
                );
                if(prevNode != null) {prevNode.moveToFirst();}
                Log.e(TAG, "Replace Tail Function: Tail replaced with " +
                                prevNode.getColumnIndex(CounterEntry._ID));
                setTailIdPreference(prevNode.getInt(prevNode.getColumnIndex(CounterEntry._ID)));
            }
            // If a row was updated
            if(rowsUpdated != 0){
                Log.e(TAG, "Replace Tail Function: " + rowsUpdated + " rows updated");
                return true;
            } else{
                Log.i(TAG, "Replace Tail Function: 0 rows updated");
            }
            return true;
        } else{
            Log.i(TAG, "Replace Tail Function : Cursor was null");
            return false;
        }
    }

}
