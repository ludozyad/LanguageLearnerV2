package com.example.blazej.languagelearner.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by Blazej on 22.10.2017.
 */

public class WordAccountStatusContract {
    public static SQLiteDatabase myIsLearnedDB;
    public static final class WordAccountStatusColumnsEntry implements BaseColumns {
        public static final String TABLE_NAME = "word_account_status";
        public static final String POLISH_COLUMN_NAME = "polish_column";
        public static final String COLUMN_ACCOUNT_NAME = "name_column";
        public static final String COLUMN_IS_LEARNED = "is_learned_column";
        public static final String COLUMN_IS_LEARNED_COUNTER = "is_learned_counter_column";
        public static final String COLUMN_REVIEW_DATE = "review_date_column";
        public static final String CATEGORY_COLUMN_NAME = "title_column";
    }

    public static final class WordStatisticColumnsEntry implements BaseColumns {
        public static final String TABLE_NAME = "word_statistic";
        public static final String POLISH_COLUMN_NAME = "polish_column";
        public static final String COLUMN_ACCOUNT_NAME = "name_column";
        public static final String COLUMN_IS_LEARNED = "is_learned_column";
        public static final String COLUMN_LEARNED_FORGOT_DATE = "review_date_column";
        public static final String CATEGORY_COLUMN_NAME = "title_column";
    }

    public static Cursor getWordAccountStatusCursor(){
        return myIsLearnedDB.query
                (WordAccountStatusColumnsEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    public static Cursor getWordStatisticCursor(){
        return myIsLearnedDB.query
                (WordStatisticColumnsEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
    }

    public static Cursor getLearnedPolishWords(String accountName, String categoryName){  //because we have only polish words in this base
        return  WordAccountStatusContract.myIsLearnedDB.query(WordAccountStatusContract.WordAccountStatusColumnsEntry.TABLE_NAME,
                new String[] {WordAccountStatusContract.WordAccountStatusColumnsEntry.POLISH_COLUMN_NAME},
                WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_ACCOUNT_NAME + " = ? " + "AND " +
                        WordAccountStatusColumnsEntry.CATEGORY_COLUMN_NAME + " = ? " + "AND " +
                        WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_IS_LEARNED + " = ? ",
                new String[] {accountName,categoryName, "1"},
                null,
                null,
                null);
    }

    public static Cursor getWordAccountStatusCursorWithSpecificAccount(String accountName){
        return myIsLearnedDB.query
                (WordAccountStatusColumnsEntry.TABLE_NAME,
                        null,
                        WordAccountStatusColumnsEntry.COLUMN_ACCOUNT_NAME + " = ?",
                        new String[]{accountName},
                        null,
                        null,
                        null);
    }

    public static Cursor getWordAccountStatusCursorWithSpecificAccountLearned(String accountName){
        return myIsLearnedDB.query
                (WordAccountStatusColumnsEntry.TABLE_NAME,
                        null,
                        WordAccountStatusColumnsEntry.COLUMN_ACCOUNT_NAME + " = ?" + " AND " + WordAccountStatusColumnsEntry.COLUMN_IS_LEARNED + " = ?",
                        new String[]{accountName,"1"},
                        null,
                        null,
                        null);
    }

    public static boolean ifWordAccountStatusCursorContain(String word,String categoryName,String accountName, Integer isLearned){
        Cursor cursor = getWordAccountStatusCursor();
            while(cursor.moveToNext()){
                if(cursor.getString(1).equals(word)&&cursor.getString(2).equals(categoryName)&&cursor.getString(3).equals(accountName)&&cursor.getInt(4)==isLearned){
                    cursor.close();
                    return true;
                }
            }
            cursor.close();
            return false;
    }

    public static boolean ifWordStatisticCursorContain(String word,String categoryName,String accountName, Integer isLearned, String date){
        Cursor cursor = getWordStatisticCursor();
        while(cursor.moveToNext()){
            if(cursor.getString(1).equals(word)&&cursor.getString(2).equals(categoryName)&&cursor.getString(3).equals(accountName)&&cursor.getInt(4)==isLearned&&cursor.getString(5).equals(date)){
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;
    }
}
