package com.example.akash.creditmanagementsystem.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * creating database contract class CreditContract contain all the constant value for the database's tables
 */
public final class CreditContract {

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.akash.creditmanagementsystem";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * String constant path for Users table
     **/
    public static final String PATH_USERS = "users";

    /**
     * String constant path for Transfer table
     **/
    public static final String PATH_TRANSFER = "transfer";

    /**
     * This Class containing all the constant related to Creating table called Users
     **/
    public static abstract class UserEntry implements BaseColumns {

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of users.
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY +
                "/" + PATH_USERS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single users.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY +
                "/" + PATH_USERS;

        /**
         * The content URI to access the user data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_USERS);

        /**
         * All the constant for the users table
         **/
        public static final String TABLE_NAME = "users";
        public static final String _ID = BaseColumns._ID;

        public static final String USER_NAME = "name";
        public static final String USER_GENDER = "gender";
        public static final String USER_EMAIL = "email";
        public static final String USER_CREDITS = "credits";
        public static final String USER_PHONE_NUMBER = "phone_number";
    }

    /**
     * This Class containing all the constant related to Creating table called Transfer
     **/
    public static abstract class TransferEntry implements BaseColumns {

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of user.
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY +
                "/" + PATH_TRANSFER;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single users.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY +
                "/" + PATH_TRANSFER;


        /**
         * The content URI to access the transfer's user data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TRANSFER);

        /**
         * All the constant for the Transfer table
         **/
        public static final String TABLE_NAME = "transfer";
        public static final String _ID = BaseColumns._ID;

        public static final String TRANSFER_FROM_USER_NAME = "from_user_name";
        public static final String TRANSFER_TO_USER_NAME = "to_user_name";
        public static final String TRANSFER_CREDITS = "credits";
    }
}
