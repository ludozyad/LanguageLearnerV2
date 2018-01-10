package com.example.blazej.languagelearner.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Blazej on 15.10.2017.
 */

public class WordsDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "words.db";
    private static final int DATABASE_VERSION = 1;
    private Context context;

    public WordsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + WordListContract.WordListColumnsEntry.TABLE_NAME +  " (" +
                WordListContract.WordListColumnsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                WordListContract.WordListColumnsEntry.GERMAN_COLUMN_NAME + " TEXT NOT NULL, " +
                WordListContract.WordListColumnsEntry.POLISH_COLUMN_NAME + " TEXT NOT NULL, " +
                WordListContract.WordListColumnsEntry.CATEGORY_COLUMN_NAME + " TEXT NOT NULL, " +
                WordListContract.WordListColumnsEntry.CATEGORY_COUNT_COLUMN_NAME + " INTEGER NOT NULL " +
                "); ";
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WordListContract.WordListColumnsEntry.TABLE_NAME);
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
                cv.put(WordListContract.WordListColumnsEntry.GERMAN_COLUMN_NAME, m.getKey());
                cv.put(WordListContract.WordListColumnsEntry.POLISH_COLUMN_NAME, m.getValue());
                cv.put(WordListContract.WordListColumnsEntry.CATEGORY_COLUMN_NAME, headingList.get(i));
                cv.put(WordListContract.WordListColumnsEntry.CATEGORY_COUNT_COLUMN_NAME, wordsInEachCategory[i]);
                db.insert(WordListContract.WordListColumnsEntry.TABLE_NAME, null, cv);
                cv.clear();
            }
        }
    }
}

