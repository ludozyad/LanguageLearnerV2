package com.example.blazej.languagelearner;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blazej.languagelearner.R;import com.example.blazej.languagelearner.data.AccountListContract;
import com.example.blazej.languagelearner.data.WordAccountStatusContract;

/**
 * Created by Blazej on 10.10.2017.
 */



public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.AccountViewHolder> {

    interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

    private static final String TAG = "MyActivity";
    private static ClickListener clickListener;
    // Holds on to the cursor to display accounts
    private Cursor mCursor;
    private Context mContext;


    public AccountListAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }


    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.account_list_item, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AccountViewHolder holder, int position) {
        // Move the mCursor to the position of the item to be displayed
        if (!mCursor.moveToPosition(position))
            return; // bail if returned null
        // Update the view holder with the information needed to display
        String name = mCursor.getString(mCursor.getColumnIndex(AccountListContract.AccountListEntry.COLUMN_ACCOUNT_NAME));

        long id = mCursor.getLong(mCursor.getColumnIndex(AccountListContract.AccountListEntry._ID));
        // Display the guest name

        holder.itemView.setOnTouchListener((new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toast.makeText(mContext, R.string.hold_to_delete, Toast.LENGTH_SHORT).show();
                return false;
            }
        }));

        holder.nameTextView.setText(name);
        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        // Always close the previous mCursor first
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    class AccountViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        // Will display account name
        TextView nameTextView;

        public AccountViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            nameTextView = (TextView)itemView.findViewById(R.id.name_text_view);
            nameTextView.setTextColor(Color.WHITE);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(getAdapterPosition(), v);
            long id = (long) v.getTag();
            String name = "'" + nameTextView.getText().toString() + "'";
            removeGuest(id,name);
            AccountListContract.mAdapter.swapCursor(getAllGuests());
            return false;
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        AccountListAdapter.clickListener = clickListener;
    }

    private void removeGuest(long id, String name) {
         AccountListContract.accountDB.delete(AccountListContract.AccountListEntry.TABLE_NAME, AccountListContract.AccountListEntry._ID + "=" + id, null);
         WordAccountStatusContract.myIsLearnedDB.delete(WordAccountStatusContract.DatabaseColumnsEntry.TABLE_NAME,WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_ACCOUNT_NAME + "=" + name, null);
    }

    public static Cursor getAllGuests() {
        return AccountListContract.accountDB.query(
                AccountListContract.AccountListEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

}
