package com.example.blazej.languagelearner;

import android.accounts.Account;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.blazej.languagelearner.LoginActivity;import com.example.blazej.languagelearner.R;import com.example.blazej.languagelearner.data.AccountListContract;

public class RegisterActivity extends AppCompatActivity {

    private EditText mNewAccountNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mNewAccountNameEditText = (EditText)findViewById(R.id.name_edit_text);
    }

    public void addToAccountList(View view) {
        if (mNewAccountNameEditText.getText().length() == 0) {
            Toast.makeText(this,R.string.empty_field,Toast.LENGTH_SHORT).show();
            return;
        }
        Cursor accListCursor = AccountListContract.getAccountCursor();
        while(accListCursor.moveToNext()){
            if(accListCursor.getString(1).equals(mNewAccountNameEditText.getText().toString())){
                Toast.makeText(this, R.string.double_user,Toast.LENGTH_SHORT).show();
                mNewAccountNameEditText.getText().clear();
                return;
            }
        }
        // Add guest info to mDb
        addNewAccount(mNewAccountNameEditText.getText().toString());
        AccountListContract.mAdapter.swapCursor(AccountListAdapter.getAllGuests());
    }

    private long addNewAccount(String name) {
        ContentValues cv = new ContentValues();
        cv.put(AccountListContract.AccountListEntry.COLUMN_ACCOUNT_NAME, name);
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        return AccountListContract.accountDB.insert(AccountListContract.AccountListEntry.TABLE_NAME, null, cv);
    }
}
