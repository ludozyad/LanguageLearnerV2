package com.example.blazej.languagelearner.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Blazej on 22.10.2017.
 */

public class WordAccountStatusDbHelper extends SQLiteOpenHelper {
    // The database name
    private static final String DATABASE_NAME = "words_account_status.db";
    private static final int DATABASE_VERSION = 1;
    private Context context;

    public WordAccountStatusDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v("TAG", "Tworzymy tabele tu XD");
        String SQL_CREATE_TABLE = "CREATE TABLE " + WordAccountStatusContract.DatabaseColumnsEntry.TABLE_NAME +  " (" +
                WordAccountStatusContract.DatabaseColumnsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                WordAccountStatusContract.DatabaseColumnsEntry.POLISH_COLUMN_NAME + " TEXT NOT NULL, " +
                WordAccountStatusContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME + " TEXT NOT NULL, " +
                WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_ACCOUNT_NAME + " TEXT NOT NULL, " +
                WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_IS_LEARNED + " BOOLEAN NOT NULL " +
                "); ";
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v("TAG", "Tworzymy tabele tu XD v2");
        db.execSQL("DROP TABLE IF EXISTS " + WordAccountStatusContract.DatabaseColumnsEntry.TABLE_NAME);
        onCreate(db);
    }
}
