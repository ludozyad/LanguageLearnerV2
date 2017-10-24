package com.example.blazej.languagelearner.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Blazej on 22.10.2017.
 */

public class WordAccountStatusContract {
    public static SQLiteDatabase myIsLearnedDB;
    public static final class DatabaseColumnsEntry implements BaseColumns {

        public static final String TABLE_NAME = "word_account_status";
        public static final String POLISH_COLUMN_NAME = "polish_column";
        public static final String COLUMN_ACCOUNT_NAME = "name_column";
        public static final String COLUMN_IS_LEARNED = "is_learned_column";
        public static final String CATEGORY_COLUMN_NAME = "title_column";
    }
    public static Cursor getWordAccountStatusCursor(){
        return myIsLearnedDB.query
                (DatabaseColumnsEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    public static Cursor getWordAccountStatusCursorWithSpecificAccount(String accountName){
        return myIsLearnedDB.query
                (DatabaseColumnsEntry.TABLE_NAME,
                        null,
                        DatabaseColumnsEntry.COLUMN_ACCOUNT_NAME + " = ?",
                        new String[]{accountName},
                        null,
                        null,
                        null);
    }

    public static boolean ifWordAccountStatusCursorContain(String word,String categoryName,String accountName){
        Cursor cursor = getWordAccountStatusCursor();
            while(cursor.moveToNext()){
                if(cursor.getString(1).equals(word)&&cursor.getString(2).equals(categoryName)&&cursor.getString(3).equals(accountName)){
                    return true;
                }
            }
        cursor.close();
            return false;
    }
}
