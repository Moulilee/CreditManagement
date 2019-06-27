package com.example.akash.creditmanagementsystem;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.akash.creditmanagementsystem.data.CreditContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UsersActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.users_recycler_view)
    RecyclerView mUserRecyclerView;

    private static final int CREDIT_LOADER = 1;
    private CustomAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        ButterKnife.bind(this);


        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mUserRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new CustomAdapter(this, null);
        mUserRecyclerView.setAdapter(mAdapter);
        getLoaderManager().initLoader(CREDIT_LOADER, null, UsersActivity.this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        //creating cursor object
        String projection[] = {CreditContract.UserEntry._ID, CreditContract.UserEntry.USER_NAME, CreditContract.UserEntry.USER_GENDER,
                CreditContract.UserEntry.USER_CREDITS, CreditContract.UserEntry.USER_EMAIL, CreditContract.UserEntry.USER_PHONE_NUMBER};
        return new CursorLoader(this,
                CreditContract.UserEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        //update CustomAdapter with this new cursor containing updated User data
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        //this callback when data need to be deleted
        mAdapter.swapCursor(null);
    }
}
