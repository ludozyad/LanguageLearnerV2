package com.example.blazej.languagelearner.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Blazej on 22.10.2017.
 */

public class WordAccountStatusDbHelper extends SQLiteOpenHelper {
    // The database name
    private static final String DATABASE_NAME = "words.db";
    private static final int DATABASE_VERSION = 1;
    private Context context;

    public WordAccountStatusDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
