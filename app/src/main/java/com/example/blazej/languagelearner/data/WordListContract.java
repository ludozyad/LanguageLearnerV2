package com.example.blazej.languagelearner.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Blazej on 13.10.2017.
 */

public class WordListContract {
    public final static String wordListUrl = "http://wordki.pl/niemiecki/";
    public final static String categoriesHtmlPlace = "div#poLewej > a";
    public final static String baseSiteUll = "http://wordki.pl/";
    public final static String wordsTables = "table#slowa > tbody > tr";
    public static ArrayList<String> headingList;
    public static SQLiteDatabase myWordsDB;


    public static final class DatabaseColumnsEntry implements BaseColumns {
        public static final String TABLE_NAME = "words";
        public static final String GERMAN_COLUMN_NAME = "german_column";
        public static final String POLISH_COLUMN_NAME = "polish_column";
        public static final String CATEGORY_COLUMN_NAME = "title_column";
        public static final String CATEGORY_COUNT_COLUMN_NAME = "category_count";
    }

    public static Cursor getAllWordsTableCursor() {
        return myWordsDB.query(
                WordListContract.DatabaseColumnsEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WordListContract.DatabaseColumnsEntry._ID
        );
    }

    public static Cursor getAllCategories(){
        return myWordsDB.query(
                WordListContract.DatabaseColumnsEntry.TABLE_NAME,
                new String[] {WordListContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME},
                null,
                null,
                WordListContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME,
                null,
                WordListContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME
        );
    }

    public static Cursor getWordsInCategory(String category){
        return  myWordsDB.query(WordListContract.DatabaseColumnsEntry.TABLE_NAME,
                new String[] {WordListContract.DatabaseColumnsEntry.GERMAN_COLUMN_NAME,WordListContract.DatabaseColumnsEntry.POLISH_COLUMN_NAME},
                WordListContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME + " = ?",
                new String[]{category},
                null,
                null,
                null);
    }

    public static int getCategoryCount(String category){
        Cursor myCursor = myWordsDB.query(
                WordListContract.DatabaseColumnsEntry.TABLE_NAME,
                new String[] {WordListContract.DatabaseColumnsEntry.CATEGORY_COUNT_COLUMN_NAME},
                WordListContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME + "=?",
                new String[] {category},
                null,
                null,
                null,
                "1"
        );
        myCursor.moveToFirst();
        int catCount = myCursor.getInt(0);
        myCursor.close();
        return catCount;
    }

    public static Cursor getAllWordsByArray(String[] wordList, String[] categoryNames){

        String[] wordListAndCategoryNames = joinArrayGeneric(wordList,categoryNames);


        String whereClause = "( ";
        if(wordList.length > 0) {
            for (int i = 0; i < wordList.length; i++) {
               if (i == wordList.length - 1) {
                    whereClause += WordListContract.DatabaseColumnsEntry.POLISH_COLUMN_NAME + " = ?";
                } else {
                    whereClause += WordListContract.DatabaseColumnsEntry.POLISH_COLUMN_NAME + " = ?" + " OR ";
                }
            }
            whereClause += ") AND (";
            for (int i = 0; i < wordList.length; i++) {
                if (i == wordList.length - 1) {
                    whereClause += DatabaseColumnsEntry.CATEGORY_COLUMN_NAME + " = ?";
                } else {
                    whereClause += DatabaseColumnsEntry.CATEGORY_COLUMN_NAME + " = ?" + " OR ";
                }
            }
            whereClause += " )";

            Log.v("TAG", "where Clause: " + whereClause);
            return myWordsDB.query(
                    WordListContract.DatabaseColumnsEntry.TABLE_NAME,
                    null,
                    whereClause,
                    wordListAndCategoryNames,
                    null,
                    null,
                    null
            );
        }else{
            return null;
        }
    }

    static <T> T[] joinArrayGeneric(T[]... arrays) {
        int length = 0;
        for (T[] array : arrays) {
            length += array.length;
        }

        //T[] result = new T[length];
        final T[] result = (T[]) Array.newInstance(arrays[0].getClass().getComponentType(), length);

        int offset = 0;
        for (T[] array : arrays) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }

        return result;
    }
}
