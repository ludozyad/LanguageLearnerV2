package com.example.blazej.languagelearner.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Blazej on 10.10.2017.
 */

public class AccountListDbHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "account_list.db";
    private static final int DATABASE_VERSION = 1;

    public AccountListDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ACCOUNT_LIST_TABLE = "CREATE TABLE " + AccountListContract.AccountListColumnsEntry.TABLE_NAME + " (" +
                AccountListContract.AccountListColumnsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                AccountListContract.AccountListColumnsEntry.COLUMN_ACCOUNT_NAME + " TEXT NOT NULL " +
                "); ";
        db.execSQL(SQL_CREATE_ACCOUNT_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AccountListContract.AccountListColumnsEntry.TABLE_NAME);
        onCreate(db);
    }
}
