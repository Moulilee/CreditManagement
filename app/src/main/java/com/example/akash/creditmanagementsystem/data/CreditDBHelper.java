package com.example.akash.creditmanagementsystem.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.akash.creditmanagementsystem.UserData;
import com.example.akash.creditmanagementsystem.data.CreditContract.TransferEntry;
import com.example.akash.creditmanagementsystem.data.CreditContract.UserEntry;

import java.util.ArrayList;
import java.util.List;

public class CreditDBHelper extends SQLiteOpenHelper {

    private Context context;
    private List<UserData> mUserDataList;

    /**
     * Log tag for CreditDBHelper class
     **/
    private static final String TAG = CreditDBHelper.class.getSimpleName();

    /**
     * constant value for the database name
     **/
    public static final String DATABASE_NAME = "creditManagement.db";

    /**
     * constant value for the database version. If we change the database schema then we need to change its version
     **/
    public static final int DATABASE_VERSION = 1;

    public CreditDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /**
     * Create Table statement to create User Table in CreditManagement database
     **/
    private static final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + UserEntry.TABLE_NAME + " ("
            + UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + UserEntry.USER_NAME + " TEXT NOT NULL, "
            + UserEntry.USER_GENDER + " TEXT NOT NULL, "
            + UserEntry.USER_CREDITS + " INTEGER NOT NULL, "
            + UserEntry.USER_EMAIL + " TEXT, "
            + UserEntry.USER_PHONE_NUMBER + " TEXT);";

    /**
     * Create Table statement to create Transfer Table in CreditManagement database
     **/

    private static final String SQL_CREATE_TRANSFER_TABLE = "CREATE TABLE " + TransferEntry.TABLE_NAME + " ("
            + TransferEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TransferEntry.TRANSFER_FROM_USER_NAME + " TEXT NOT NULL, "
            + TransferEntry.TRANSFER_TO_USER_NAME + " TEXT NOT NULL, "
            + TransferEntry.TRANSFER_CREDITS + " INTEGER NOT NULL);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create tables transfer and users
        db.execSQL(SQL_CREATE_USER_TABLE);
        db.execSQL(SQL_CREATE_TRANSFER_TABLE);

        mUserDataList = new ArrayList<>();

        //create dummy data for the Users table
        mUserDataList = new ArrayList<>();
        mUserDataList.add(new UserData("James Butt", "Male", 59,
                "jbutt@gmail.com", "504-621-8927"));
        mUserDataList.add(new UserData("Lenna Paprocki", "Female", 70,
                "lpaprocki@hotmail.com", "907-385-4412"));
        mUserDataList.add(new UserData("Josephine Darakjy", "Male", 60,
                "josephine_darakjy@darakjy.org", "810-292-9388"));
        mUserDataList.add(new UserData("Daniel Foller", "Male", 40,
                "DanielUK@gmail.com", "513-570-1893"));
        mUserDataList.add(new UserData("Simona Morasca", "Female", 90,
                "simona@morasca.com", "419-503-2484"));
        mUserDataList.add(new UserData("Willard Kolmetz", "Male", 29,
                "willard@hotmail.com", "972-303-9197"));
        mUserDataList.add(new UserData("Minna Amigon", "Female", 62,
                "minna_amigon@yahoo.com", "215-874-1229"));
        mUserDataList.add(new UserData("Cammy Albares", "Female", 49,
                "calbares@gmail.com", "956-537-6195"));
        mUserDataList.add(new UserData("Dyan Oldroyd", "Male", 50,
                "doldroyd@aol.com", "913-413-4604"));
        mUserDataList.add(new UserData("Erick Ferencz", "Male", 22,
                "erick.ferencz@aol.com", "907-741-1044"));

        //insert dummy data
        for (int i = 0; i < mUserDataList.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(UserEntry.USER_NAME, mUserDataList.get(i).getUserName());
            values.put(UserEntry.USER_GENDER, mUserDataList.get(i).getUserGender());
            values.put(UserEntry.USER_CREDITS, mUserDataList.get(i).getUserCredit());
            values.put(UserEntry.USER_EMAIL, mUserDataList.get(i).getUserEmail());
            values.put(UserEntry.USER_PHONE_NUMBER, mUserDataList.get(i).getPhoneNumber());

            long rows = db.insert(UserEntry.TABLE_NAME, null, values);
            if (rows != -1) {
                Log.i(TAG, "Successful in inserting dummy data");
            } else {
                Log.i(TAG, "Error on inserting data");
            }

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop the existing tables
        db.execSQL("DROP TABLE IF EXISTS " + TransferEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME);

        //recreate the database when database version changes or its schema
        onCreate(db);
    }
}
