package com.ceri.android.ucounter.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ceri.android.ucounter.data.db.CounterContract;
import com.ceri.android.ucounter.data.db.CounterContract.CounterEntry;

import java.util.ArrayList;

public class CounterDataController {
    private Context mContext;
    private ContentResolver mContentResolver;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.ceri.android.counterprefs";
    public CounterDataController(){
    }

    public void setContext(Object context){
        mContext = (Context) context;
        mContentResolver = mContext.getContentResolver();
    }

    // TODO: Create a Linked List that will keep the data sorted
    public boolean insertCounterAtEnd(String name, int initialValue, int currentPosition) {
        return true;
    }

    public boolean appendCounter(String name, int initialValue){
        mPreferences = mContext.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE);
        ContentValues contentValues = new ContentValues();
        contentValues.put(CounterContract.CounterEntry.COLUMN_COUNTER_NAME, name);
        contentValues.put(CounterContract.CounterEntry.COLUMN_COUNTER_COUNT, initialValue);
        contentValues.put(CounterContract.CounterEntry.COLUMN_COUNTER_NEXT, -1);

        int tailId = mPreferences.getInt("tailId", -1);

        int rowCount =
                (mContentResolver.call(CounterEntry.CONTENT_URI,"getRowCount",
                        null, null).getInt("numRows"));
        if (rowCount == 0) { // If table is empty, add a head
            Log.e("Controller", "No head found.");
            insertHead();
        } else{
            Log.e("Controller", "Rows: " + rowCount);
        }

        if ( tailId == -1) { // Not yet initialized
            // TODO: Check if SharedPreferences was deleted
            Log.e(this.getClass().getSimpleName(), "Tail Id was -1");
            updateHead(contentValues);
        } else{ // Only one value
            Log.e(this.getClass().getSimpleName(), "Tail Id was not -1");
            updateTail(contentValues, tailId);
        }
        return true;
    }


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
        // Keep moving through the linked list until you reach the end
        while(currentNode != -1){
            // Given a node's id, it finds the next value of the node
            Cursor cursor = content.query(
                    CounterEntry.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null);
            // Sets the current node to be the next's
            currentNode = cursor.getInt(cursor.getColumnIndex(CounterEntry.COLUMN_COUNTER_NEXT));
            selectionArgs[0] = String.valueOf(currentNode);

            // Add the found node to the array list
            arrayList.add(currentNode);
        }
        return arrayList;
    }

    // Queries through the entire linked list until it finds the id given
    private static int findNextId(Cursor cursor, int id){
        cursor.moveToFirst();
        while(cursor.moveToNext()){
            if(cursor.getInt(cursor.getColumnIndex(CounterEntry._ID)) == id){
                return cursor.getInt(cursor.getColumnIndex(CounterEntry.COLUMN_COUNTER_NEXT));
            }
        }
        return -1;
    }

    public boolean updateCounter(int id, String name, int oldValue, int step){
        // Track the number of rows updated (should be either zero or one)
        int rowsUpdated;

        // Set up the new value to be set in the database
        ContentValues contentValue = new ContentValues();
        contentValue.put(CounterContract.CounterEntry.COLUMN_COUNTER_COUNT, oldValue + step);

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

    public boolean deleteCounter(int id){
        return true;
    }

    private void insertHead(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CounterEntry.COLUMN_COUNTER_NAME, "DEFAULT");
        contentValues.put(CounterEntry.COLUMN_COUNTER_COUNT, "1");
        contentValues.put(CounterEntry.COLUMN_COUNTER_NEXT, "-1");
        mContentResolver.insert(CounterEntry.CONTENT_URI, contentValues);
        Log.e("InsertHead", "Value inserted");
        // Edit SharedPreferences to have it's tail point toward the new counter
        setTailIdPreference(1);
    }

    private void setTailIdPreference(int id){
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putInt("tailId", id);
        preferencesEditor.apply();
    }
    private void updateHead(ContentValues contentValue){
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

    private void updateTail(ContentValues contentValue, int lastId){
        // Create a new value in the table using the given contentValue
        Uri newCounterUri = mContentResolver.insert(CounterEntry.CONTENT_URI, contentValue);
        long newCounterId = ContentUris.parseId(newCounterUri);

        // Set value so that the head will now point to the first variable
        ContentValues nextValue = new ContentValues();
        nextValue.put(CounterContract.CounterEntry.COLUMN_COUNTER_NEXT, newCounterId);

        // Query the tail of the table
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
}
