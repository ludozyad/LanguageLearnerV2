package com.example.blazej.languagelearner.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.example.blazej.languagelearner.AccountListAdapter;

/**
 * Created by Blazej on 10.10.2017.
 */

public class AccountListContract{

    public static SQLiteDatabase accountDB;
    public static AccountListAdapter mAdapter;
    public static final String sharedName = "name";

    public static final class AccountListColumnsEntry implements BaseColumns {

        public static final String TABLE_NAME = "accounts";
        public static final String COLUMN_ACCOUNT_NAME = "name";
    }

    public static Cursor getAccountCursor(){
        return accountDB.query
                (AccountListColumnsEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
    }
}
