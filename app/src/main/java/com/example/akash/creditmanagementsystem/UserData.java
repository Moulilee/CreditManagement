package com.example.akash.creditmanagementsystem;


/***
 * It is custom User data class that define various attribute for each user
 */

public class UserData {

    private String mUserName;
    private String mUserGender;
    private int mUserCredit;
    private String mUserEmail;
    private String mPhoneNumber;

    /**
     * @param mUserName    define user's name
     * @param mUserGender  define user's gender
     * @param mUserCredit  define user's credit points
     * @param mUserEmail   define user's email
     * @param mPhoneNumber define user's phone number
     */
    public UserData(String mUserName, String mUserGender, int mUserCredit, String mUserEmail, String mPhoneNumber) {
        this.mUserName = mUserName;
        this.mUserGender = mUserGender;
        this.mUserCredit = mUserCredit;
        this.mUserEmail = mUserEmail;
        this.mPhoneNumber = mPhoneNumber;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getUserGender() {
        return mUserGender;
    }

    public int getUserCredit() {
        return mUserCredit;
    }

    public String getUserEmail() {
        return mUserEmail;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }
}
