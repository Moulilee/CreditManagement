package com.example.akash.creditmanagementsystem;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.akash.creditmanagementsystem.data.CreditContract.UserEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * This is custom adapter class that extends from CursorRecyclerViewAdapter<> class.
 * it is used to create a view(ViewHolder's object) form data item from the database,
 * and provide it to the RecyclerView's layoutManager
 */

public class CustomAdapter extends CursorRecyclerViewAdapter<CustomAdapter.ViewHolder> {

    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_name_text_view)
        TextView mUserName;
        @BindView(R.id.credits_text_view)
        TextView mUserCredits;
        @BindView(R.id.user_pic_image_view)
        ImageView mUserImage;
        @BindView(R.id.transfer_button)
        Button mTransferCreditButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public CustomAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.mContext = context;
    }

    @Override
    public void onBindViewHolder(final CustomAdapter.ViewHolder viewHolder, Cursor cursor) {

        /**getting required data from the database by using cursor**/
        String userName = cursor.getString(cursor.getColumnIndex(UserEntry.USER_NAME));
        int userCredits = cursor.getInt(cursor.getColumnIndex(UserEntry.USER_CREDITS));
        String userGender = cursor.getString(cursor.getColumnIndex(UserEntry.USER_GENDER));

        /**Changing the user profile picture based on the user gender**/
        if (userGender.equals(mContext.getString(R.string.user_gender_male))) {
            viewHolder.mUserImage.setImageResource(R.drawable.ic_man);
        } else if (userGender.equals(mContext.getString(R.string.user_gender_female))) {
            viewHolder.mUserImage.setImageResource(R.drawable.ic_woman);
        }

        /**set the value of user's id, name and credits**/
        viewHolder.mUserName.setText(userName);
        viewHolder.mUserCredits.setText(String.valueOf(userCredits));

        /**get credit value for selected user**/
        cursor.moveToPosition(viewHolder.getAdapterPosition());
        final int credits = cursor.getInt(cursor.getColumnIndex(UserEntry.USER_CREDITS));

        viewHolder.mTransferCreditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check whether the selected user have enough credits(assume its > 1)

                if (credits <= 1) {
                    Toast.makeText(mContext, R.string.warning_msg_less_credits, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(mContext, TransferCreditsActivity.class);
                int id = viewHolder.getAdapterPosition();
                Uri currentUserUri = ContentUris.withAppendedId(UserEntry.CONTENT_URI, getItemId(id));
                intent.setData(currentUserUri);
                mContext.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_users, viewGroup, false);
        return new ViewHolder(view);
    }
}
