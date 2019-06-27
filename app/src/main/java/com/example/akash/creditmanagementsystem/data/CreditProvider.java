package com.example.akash.creditmanagementsystem.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.akash.creditmanagementsystem.R;

/***
 * This class used to retrieve data form the database indirectly and provide it to the UI of the app.
 * This also help to validate the input data.(Adding data validation).
 * Its also work well with other framework class, here CursorLoader..
 */

public class CreditProvider extends ContentProvider {

    /**
     * URI matcher code for the content URI for the users table
     */
    private static final int USERS = 100;

    /**
     * URI matcher code for the content URI for a single user inside the users table
     */
    private static final int USERS_ID = 101;

    /**
     * URI matcher code for the content URI for a single user inside the users table on based on user name
     */
    private static final int USERS_NAME = 104;

    /**
     * URI matcher code for the content URI for the transfer table
     */
    private static final int TRANSFER = 102;

    /**
     * URI matcher code for the content URI for a single user inside the transfer table
     */
    private static final int TRANSFER_ID = 103;


    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        //Uri pattern for users table
        sUriMatcher.addURI(CreditContract.CONTENT_AUTHORITY, CreditContract.PATH_USERS, USERS);
        sUriMatcher.addURI(CreditContract.CONTENT_AUTHORITY, CreditContract.PATH_USERS + "/#", USERS_ID);
        sUriMatcher.addURI(CreditContract.CONTENT_AUTHORITY, CreditContract.PATH_USERS + "/*", USERS_NAME);

        //Uri pattern for transfer table
        sUriMatcher.addURI(CreditContract.CONTENT_AUTHORITY, CreditContract.PATH_TRANSFER, TRANSFER);
        sUriMatcher.addURI(CreditContract.CONTENT_AUTHORITY, CreditContract.PATH_TRANSFER + "/#", TRANSFER_ID);
    }

    /**
     * constant value for LOG_TAG
     **/
    private static final String LOG_TAG = CreditProvider.class.getSimpleName();


    /**
     * create database helper object
     **/
    private CreditDBHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new CreditDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // This cursor will hold the result of the query
        Cursor cursor;

        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        switch (match) {
            case USERS:
                cursor = database.query(CreditContract.UserEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case USERS_ID:
                selection = CreditContract.UserEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(CreditContract.UserEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case USERS_NAME:
                cursor = database.query(CreditContract.UserEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.error_query_database) + uri);
        }

        //set notification URI on the cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case USERS:
                return CreditContract.UserEntry.CONTENT_LIST_TYPE;
            case USERS_ID:
                return CreditContract.UserEntry.CONTENT_ITEM_TYPE;
            case TRANSFER:
                return CreditContract.TransferEntry.CONTENT_LIST_TYPE;
            case TRANSFER_ID:
                return CreditContract.TransferEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                //no need to insert data into user table because we already inserted dummy data into it.
                return null;
            case TRANSFER:
                return insertTransferData(uri, values);
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.error_insertion_failed) + uri);
        }
    }

    private Uri insertTransferData(Uri uri, ContentValues values) {
        //check that the transfer user_name cannot be null
        String transferFromUserName = values.getAsString(CreditContract.TransferEntry.TRANSFER_FROM_USER_NAME);
        if (transferFromUserName == null) {
            throw new IllegalArgumentException("User transfer ID cannot be null");
        }

        //check that the receive user_id cannot be null
        String transferToUserName = values.getAsString(CreditContract.TransferEntry.TRANSFER_TO_USER_NAME);
        if (transferToUserName == null) {
            throw new IllegalArgumentException("User receive ID cannot be null");
        }

        //check that the user credits cannot be less than 1
        int userCredits = values.getAsInteger(CreditContract.TransferEntry.TRANSFER_CREDITS);
        if (userCredits < 1) {
            throw new IllegalArgumentException("Users credits cannot be less than 1");
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id1 = database.insert(CreditContract.TransferEntry.TABLE_NAME, null, values);
        if (id1 == -1) {
            Log.e(LOG_TAG, getContext().getString(R.string.log_msg_insert_failed) + uri);
        }

        //Notify all the listeners that the has changed for the transfer content URI
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id1);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        //get writable database object
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS_ID:
                selection = CreditContract.UserEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateUserData(uri, values, selection, selectionArgs);
            case USERS:
                int rowsUpdated = database.update(CreditContract.UserEntry.TABLE_NAME, values, selection, selectionArgs);
                if (rowsUpdated != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsUpdated;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.error_update_failed) + uri);
        }
    }

    private int updateUserData(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        //check that the user credits cannot be less than 5
        if (values.containsKey(CreditContract.UserEntry.USER_CREDITS)) {
            int userId = values.getAsInteger(CreditContract.UserEntry.USER_CREDITS);
            if (userId < 1) {
                throw new IllegalArgumentException("User credits cannot be less than 1");
            }
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(CreditContract.UserEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        //Return the number of rows updated
        return rowsUpdated;
    }
}
