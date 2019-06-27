package com.example.akash.creditmanagementsystem;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.akash.creditmanagementsystem.data.CreditContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransferCreditsActivity extends AppCompatActivity {

    @BindView(R.id.spinner_user_names)
    Spinner mUserNameSpinner;
    @BindView(R.id.transfer_user_name_text_view)
    TextView mUserNameTextView;
    @BindView(R.id.transfer_user_credits_text_view)
    TextView mUserCreditsTextView;
    @BindView(R.id.receive_user_credit_edit_text)
    EditText mReceiveUserCreditEditText;
    @BindView(R.id.transfer_credits_button)
    Button mTransferCreditButton;
    @BindView(R.id.receive_user_credits_text_view)
    TextView mReceiveUserCreditsTextView;
    @BindView(R.id.transfer_credits_parent_activity)
    ConstraintLayout mParentActivity;


    private List<String> mUsersNameList;


    private Uri mCurrentUserUri;


    private Cursor mCursor;


    private String mUserName;


    private int mUserCredits;


    private String mReceiveUserName = null;


    private int mOldCredits;


    private String mSelection;
    private String mSelectionArgs[];


    private int mNewCredits;


    private String mCreditsValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_credits);
        ButterKnife.bind(this);


        Intent intent = getIntent();
        mCurrentUserUri = intent.getData();


        String projection[] = {CreditContract.UserEntry._ID, CreditContract.UserEntry.USER_NAME, CreditContract.UserEntry.USER_GENDER,
                CreditContract.UserEntry.USER_CREDITS, CreditContract.UserEntry.USER_EMAIL, CreditContract.UserEntry.USER_PHONE_NUMBER};
        if (mCurrentUserUri != null) {
            mCursor = getContentResolver().query(mCurrentUserUri,
                    projection,
                    null,
                    null,
                    null);
            Log.i("in transfer activity", mCursor.toString());
        } else {
            throw new IllegalArgumentException("Uri cannot be null");
        }


        if (mCursor.moveToFirst()) {
            mUserName = mCursor.getString(mCursor.getColumnIndex(CreditContract.UserEntry.USER_NAME));
            mUserCredits = mCursor.getInt(mCursor.getColumnIndex(CreditContract.UserEntry.USER_CREDITS));

            mUserNameTextView.setText(mUserName);
            mUserCreditsTextView.setText(String.valueOf(mUserCredits));
        }


        mUsersNameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.userName)));
        if (mUsersNameList.contains(mUserName)) {
            mUsersNameList.remove(mUserName);
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mUsersNameList);
        mUserNameSpinner.setAdapter(adapter);
        mUserNameSpinner.setSelected(false);
        mUserNameSpinner.setSelection(0, false);



        mUserNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mReceiveUserName = parent.getItemAtPosition(position).toString();


                String projection[] = {CreditContract.UserEntry._ID, CreditContract.UserEntry.USER_NAME, CreditContract.UserEntry.USER_GENDER,
                        CreditContract.UserEntry.USER_CREDITS, CreditContract.UserEntry.USER_EMAIL, CreditContract.UserEntry.USER_PHONE_NUMBER};
                mSelection = CreditContract.UserEntry.USER_NAME + "=?";
                mSelectionArgs = new String[]{mReceiveUserName};
                Cursor cursor = getContentResolver().query(CreditContract.UserEntry.CONTENT_URI, projection, mSelection, mSelectionArgs, null);


                if (cursor.moveToFirst()) {
                    mOldCredits = cursor.getInt(cursor.getColumnIndex(CreditContract.UserEntry.USER_CREDITS));
                }
                mReceiveUserCreditsTextView.setVisibility(View.VISIBLE);

                if (!mReceiveUserName.equals(R.string.spinner_label)) {
                    mReceiveUserCreditsTextView.setText(mReceiveUserName + " Credit's " + mOldCredits);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mTransferCreditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (mReceiveUserName == null || mReceiveUserName.equals(getString(R.string.spinner_label))) {
                    Toast.makeText(TransferCreditsActivity.this, R.string.warning_msg_empty_user_name, Toast.LENGTH_SHORT).show();
                    return;
                }


                mCreditsValue = mReceiveUserCreditEditText.getText().toString();
                if (TextUtils.isEmpty(mCreditsValue)) {
                    Toast.makeText(TransferCreditsActivity.this, R.string.warning_msg_empty_credits, Toast.LENGTH_SHORT).show();
                    return;
                }


                mNewCredits = Integer.valueOf(mCreditsValue);


                if ((mNewCredits < 1) || (mNewCredits > mUserCredits - 1)) {
                    Toast.makeText(TransferCreditsActivity.this, getString(R.string.warning_msg_exceed_credits_limit), Toast.LENGTH_LONG).show();
                    return;
                }

                showTransferCreditConfirmationDialog();
            }
        });
    }

    private void showTransferCreditConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_positive_dialog);
        builder.setPositiveButton(R.string.confirm_positive_title, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                setTransferDetails();
            }
        });
        builder.setNegativeButton(R.string.confirm_negative_title, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setTransferDetails() {

        mUserCredits -= mNewCredits;


        ContentValues values = new ContentValues();
        values.put(CreditContract.UserEntry.USER_CREDITS, mUserCredits);
        getContentResolver().update(mCurrentUserUri, values, null, null);


        int newReceiveCredits;
        newReceiveCredits = mOldCredits + mNewCredits;

        ContentValues values1 = new ContentValues();
        values1.put(CreditContract.UserEntry.USER_CREDITS, newReceiveCredits);
        getContentResolver().update(CreditContract.UserEntry.CONTENT_URI, values1, mSelection, mSelectionArgs);



        ContentValues values2 = new ContentValues();
        values2.put(CreditContract.TransferEntry.TRANSFER_FROM_USER_NAME, mUserName);
        values2.put(CreditContract.TransferEntry.TRANSFER_TO_USER_NAME, mReceiveUserName);
        values2.put(CreditContract.TransferEntry.TRANSFER_CREDITS, mNewCredits);

        Uri uri = getContentResolver().insert(CreditContract.TransferEntry.CONTENT_URI, values2);

        if (uri != null) {
            Snackbar.make(mParentActivity, R.string.transfer_credit_successful_text, Snackbar.LENGTH_LONG).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 2000);
        } else {
            Log.i(TransferCreditsActivity.class.getSimpleName(), getString(R.string.error_insertion_failed));
        }
    }

    @Override
    public void onBackPressed() {
        if (mReceiveUserName != null || !TextUtils.isEmpty(mCreditsValue)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.back_button_dialog_msg);
            builder.setPositiveButton(R.string.confirm_positive_title, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            builder.setNegativeButton(R.string.confirm_negative_title, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });


            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mReceiveUserName != null || !TextUtils.isEmpty(mCreditsValue)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.back_button_dialog_msg);
                builder.setPositiveButton(R.string.confirm_positive_title, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NavUtils.navigateUpFromSameTask(TransferCreditsActivity.this);
                    }
                });
                builder.setNegativeButton(R.string.confirm_negative_title, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });


                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            } else {
                NavUtils.navigateUpFromSameTask(this);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
