package com.ceribit.android.ucounter.data;

import android.content.ContentProvider;
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

import com.ceribit.android.ucounter.data.db.CounterContract;
import com.ceribit.android.ucounter.data.db.CounterContract.CounterEntry;
import com.ceribit.android.ucounter.ui.CounterInfo;

import java.util.ArrayList;

public class CounterDataController {

    /** Context of the view, needed to access the ContentResolver */
    private Context mContext;

    /** Links to ContentProvider to access database */
    private ContentResolver mContentResolver;

    /** Is used to store the last value in the table (may not be needed) */
    private SharedPreferences mPreferences;

    /** Holds the location of the SharedPrefFile */
    private String sharedPrefFile = "com.ceribit.android.counterprefs";

    /** Tag used for debugging with Log function */
    private static String TAG = CounterDataController.class.getSimpleName();


    public CounterDataController(){
    }


    /** Sets context member variable */
    public void setContext(Object context){
        mContext = (Context) context;
        mContentResolver = mContext.getContentResolver();
    }

    /** Updates the recorded SharedPreferences with a new ID */
    private void setTailIdPreference(int id){
        mPreferences = mContext.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putInt("tailId", id);
        preferencesEditor.apply();
    }


    // ********************
    // * Insert
    // ********************


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
    public boolean append(String name, int initialValue){
        // Update mPreferences
        mPreferences = mContext.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE);

        // Creates new counter entry
        ContentValues contentValues = new ContentValues();
        contentValues.put(CounterContract.CounterEntry.COLUMN_COUNTER_NAME, name);
        contentValues.put(CounterContract.CounterEntry.COLUMN_COUNTER_COUNT, initialValue);
        contentValues.put(CounterContract.CounterEntry.COLUMN_COUNTER_NEXT, -1);


        // Applies LinkedList insertion based on the if there was a recorded tail0
        return appendToTable(contentValues);
    }

    /** Manages insertion to the table based on the current table's condition
     * Conditions:
     *  1. Table not yet created -> Creates head and appends to default head
     *  2. Table created with stored TailId -> Appends to tail
     *  3. Table created with lost TailId -> TailId is refreshed and new counter is appended to it
     * */
    private boolean appendToTable(ContentValues contentValues){
        // Gets size of table
        int rowCount =  (mContentResolver.call(CounterEntry.CONTENT_URI,"getRowCount",
                        null, null).getInt("numRows"));

        // Initializes table if not yet created
        if (rowCount == 0) { // If table is empty, add a head
            insertHead();
        } else{
            Log.i(TAG, "AppendCounter: Table already initialized." );
        }

        // Gets shared preferences
        mPreferences = mContext.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE);

        // Appends value based on existing tail
        int tailId = refreshTail();
        if (tailId == -1) { // Not yet initialized
            Log.e(this.getClass().getSimpleName(), "Tail Id was -1");
            appendToHead(contentValues);
        } else{ // Only one value
            appendToTail(contentValues, tailId);
        }

        return true;
    }

    /** Refreshes tail in case sharedpreferences were deleted.
     *  Returns true if the tail is still -1 (meaning that the tail and head are the same)
     *      and false if the tail was refreshed and found not to be -1 */
    private int refreshTail(){
        int tailId = mPreferences.getInt("tailId", -1);
        if(tailId == -1){
            ArrayList<Integer> list = getPositionList(mContext.getContentResolver());
            if(list.size() > 1){
                tailId = list.get(list.size()-2); // Gets last (not -1)id of the nextNode list
            }
        }
        mPreferences.edit().putInt("tailId", tailId).apply();
        return tailId;
    }

    /** If the database is empty, this creates a new head */
    private void insertHead(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CounterEntry._ID, 1);
        contentValues.put(CounterEntry.COLUMN_COUNTER_NAME, "DEFAULT");
        contentValues.put(CounterEntry.COLUMN_COUNTER_COUNT, "1");
        contentValues.put(CounterEntry.COLUMN_COUNTER_NEXT, "-1");
        mContentResolver.insert(CounterEntry.CONTENT_URI, contentValues);
    }


    /** If the head is the only value, this updates it to point towards a new counter */
    private void appendToHead(ContentValues contentValue){
        Log.i(TAG, "AppendToHead : Value attached to head");

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
        // Edit SharedPreferences to have the recorded tail as the new counter
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putInt("tailId", (int) newCounterId);
        preferencesEditor.apply();
    }



    /** Updates the database linked list through the tail  */
    private void appendToTail(ContentValues contentValue, int lastId){
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



    // ********************
    // * Data Retrieval
    // ********************

    /**
     * Returns an array list of each id, where the integer's index is an ID and its placement is its
     * position in the PageAdapter
     * */
    public static ArrayList<Integer> getPositionListFromView(Object view){
        ContentResolver content = ((Context)view).getContentResolver();
        return getPositionList(content);
    }

    /** Gets Position List given a content resolver */
    private static ArrayList<Integer> getPositionList(ContentResolver contentResolver){
        // Initialize query variables
        String[] projection = {
                CounterEntry._ID,
                CounterEntry.COLUMN_COUNTER_NEXT
        };
        String selection = CounterEntry._ID + "=?";
        String[] selectionArgs = {"1"};
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(1);

        // Keep moving through the linked list until you reach the end
        int currentNode = 1;
        while(currentNode != -1){
            // Given a node's id, it finds the next value of the node
            Cursor cursor = contentResolver.query(
                    CounterEntry.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null);
            cursor.moveToPosition(0);

            // Sets the current node to be the next's
            try{
                currentNode = cursor.getInt(cursor.getColumnIndex(CounterEntry.COLUMN_COUNTER_NEXT));
                selectionArgs[0] = String.valueOf(currentNode); //Move node forward in selection
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
     * Returns data of a Counter given an ID
     * @param id ID of the counter who's information you need to get
     * @return CounterInfo object containing the ID, name, value, and next attributes of a counter
     */
    public CounterInfo getCounterInfo(int id){
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


    // ********************
    // * Update
    // ********************

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

    // ********************
    // * Delete
    // ********************

    /**
     * Linked List delete function
     * @param id ID of the counter you want to delete
     * @return Successful deletion of the Counter from the database
     */
    public boolean delete(int id){
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


    /**
     * (LinkedList remove from list implementation)
     *  Given a counter's id, this replaces the pointer of the counter that points to it with the
     *  given counter's id
     *  */
    private boolean replacePrevId(int id){
        Log.e(TAG, "replacePrevId: Id=" + id);

        // Get Content Resolver
        mContentResolver = mContext.getContentResolver();
        String idSelection = CounterEntry._ID + "=?";
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

            // Return the successful of inserting the previous counter
            return updatePreviousCounter(currentNode, id);
        } else{
            // Unsuccessful update
            return false;
        }
    }

    /**
     *  Returns the update status of the counter previous of counter previous of the one that
     *  has to be deleted
     *  */
    private boolean updatePreviousCounter(Cursor currentNode, int id){
        String updateSelection = CounterEntry.COLUMN_COUNTER_NEXT + "=?";

        // Gets id of next node
        int currentNodeNext = currentNode.getInt(
                currentNode.getColumnIndex(CounterEntry.COLUMN_COUNTER_NEXT));
        currentNode.close();

        // Updates previous counter's next value
        ContentValues contentValues = new ContentValues();
        contentValues.put(CounterEntry.COLUMN_COUNTER_NEXT, currentNodeNext);
        long rowsUpdated = mContentResolver.update(
                CounterEntry.CONTENT_URI,
                contentValues,
                updateSelection,
                new String[]{String.valueOf(id)} // Searches counter where the next is the one to
                                                 // be deleted
        );

        // If delete node is at the end, update SharedPreference's tailId
        if(currentNodeNext == -1){
            // Find past node
            Cursor prevNode = mContentResolver.query( CounterEntry.CONTENT_URI,
                    null,
                    updateSelection,
                    new String[]{String.valueOf(-1)},
                    null
            );
            // Move Counter to the beginning
            prevNode.moveToFirst();
            Log.i(TAG, "Tail replaced with " + prevNode.getColumnIndex(CounterEntry._ID));
            setTailIdPreference(prevNode.getInt(prevNode.getColumnIndex(CounterEntry._ID)));
        }
        return rowsUpdated != 0;
    }
}
