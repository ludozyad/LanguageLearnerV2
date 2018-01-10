package com.example.blazej.languagelearner.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Blazej on 22.10.2017.
 */

public class WordAccountStatusDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "words_account_status.db";
    private static final int DATABASE_VERSION = 1;
    private Context context;

    public WordAccountStatusDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_FIRST_TABLE = "CREATE TABLE " + WordAccountStatusContract.WordAccountStatusColumnsEntry.TABLE_NAME +  " (" +
                WordAccountStatusContract.WordAccountStatusColumnsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                WordAccountStatusContract.WordAccountStatusColumnsEntry.POLISH_COLUMN_NAME + " TEXT NOT NULL, " +
                WordAccountStatusContract.WordAccountStatusColumnsEntry.CATEGORY_COLUMN_NAME + " TEXT NOT NULL, " +
                WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_ACCOUNT_NAME + " TEXT NOT NULL, " +
                WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_IS_LEARNED + " BOOLEAN NOT NULL, " +
                WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_IS_LEARNED_COUNTER + " INTEGER NOT NULL, " +
                WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_REVIEW_DATE + " DATE NOT NULL " +
                "); ";
        db.execSQL(SQL_CREATE_FIRST_TABLE);

        String SQL_CREATE_SECOND_TABLE = "CREATE TABLE " + WordAccountStatusContract.WordStatisticColumnsEntry.TABLE_NAME +  " (" +
                WordAccountStatusContract.WordStatisticColumnsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                WordAccountStatusContract.WordStatisticColumnsEntry.POLISH_COLUMN_NAME + " TEXT NOT NULL, " +
                WordAccountStatusContract.WordStatisticColumnsEntry.CATEGORY_COLUMN_NAME + " TEXT NOT NULL, " +
                WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_ACCOUNT_NAME + " TEXT NOT NULL, " +
                WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_IS_LEARNED + " BOOLEAN NOT NULL, " +
                WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_LEARNED_FORGOT_DATE + " DATE NOT NULL " +
                "); ";
        db.execSQL(SQL_CREATE_SECOND_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WordAccountStatusContract.WordAccountStatusColumnsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WordAccountStatusContract.WordStatisticColumnsEntry.TABLE_NAME);
        onCreate(db);
    }
}
