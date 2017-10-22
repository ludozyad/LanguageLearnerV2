package com.example.blazej.languagelearner.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Blazej on 15.10.2017.
 */

public class WordsDbHelper extends SQLiteOpenHelper {
    // The database name
    private static final String DATABASE_NAME = "words.db";
    private static final int DATABASE_VERSION = 1;
    private Context context;

    public WordsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating table
        String SQL_CREATE_TABLE = "CREATE TABLE " + WordListContract.DatabaseColumnsEntry.TABLE_NAME +  " (" +
                WordListContract.DatabaseColumnsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                WordListContract.DatabaseColumnsEntry.GERMAN_COLUMN_NAME + " TEXT NOT NULL, " +
                WordListContract.DatabaseColumnsEntry.POLISH_COLUMN_NAME + " TEXT NOT NULL, " +
                WordListContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME + " TEXT NOT NULL, " +
                WordListContract.DatabaseColumnsEntry.CATEGORY_COUNT_COLUMN_NAME + " INTEGER NOT NULL " +
                "); ";
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WordListContract.DatabaseColumnsEntry.TABLE_NAME);
        onCreate(db);
    }

    public static void fillMyBase(ArrayList<String> headingList, ArrayList<LinkedHashMap<String, String>> wordList, SQLiteDatabase db){
        if (db == null || headingList == null || wordList == null) {
            return;
        }
        int numberOfCat = headingList.size();
        int[] wordsInEachCategory = new int[numberOfCat];
        for(int i=0; i < numberOfCat; i++){
                wordsInEachCategory[i] = wordList.get(i).size();
            }

        ContentValues cv = new ContentValues();
        for(int i=0; i < numberOfCat; i++) {
            for (Map.Entry<String, String> m : wordList.get(i).entrySet()) {
                cv.put(WordListContract.DatabaseColumnsEntry.GERMAN_COLUMN_NAME, m.getKey());
                cv.put(WordListContract.DatabaseColumnsEntry.POLISH_COLUMN_NAME, m.getValue());
                cv.put(WordListContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME, headingList.get(i));
                cv.put(WordListContract.DatabaseColumnsEntry.CATEGORY_COUNT_COLUMN_NAME, wordsInEachCategory[i]);
                db.insert(WordListContract.DatabaseColumnsEntry.TABLE_NAME, null, cv);
                cv.clear();
            }
        }
    }
}

