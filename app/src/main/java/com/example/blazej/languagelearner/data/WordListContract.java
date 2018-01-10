package com.example.blazej.languagelearner.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import java.lang.reflect.Array;
import java.util.ArrayList;

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


    public static final class WordListColumnsEntry implements BaseColumns {
        public static final String TABLE_NAME = "words";
        public static final String GERMAN_COLUMN_NAME = "german_column";
        public static final String POLISH_COLUMN_NAME = "polish_column";
        public static final String CATEGORY_COLUMN_NAME = "title_column";
        public static final String CATEGORY_COUNT_COLUMN_NAME = "category_count";
    }

    public static Cursor getAllWordsTableCursor() {
        return myWordsDB.query(
                WordListContract.WordListColumnsEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WordListContract.WordListColumnsEntry._ID
        );
    }

    public static Cursor getAllCategories(){
        return myWordsDB.query(
                WordListContract.WordListColumnsEntry.TABLE_NAME,
                new String[] {WordListContract.WordListColumnsEntry.CATEGORY_COLUMN_NAME},
                null,
                null,
                WordListContract.WordListColumnsEntry.CATEGORY_COLUMN_NAME,
                null,
                WordListContract.WordListColumnsEntry.CATEGORY_COLUMN_NAME
        );
    }

    public static Cursor getWordsInCategory(String category){
        return  myWordsDB.query(WordListContract.WordListColumnsEntry.TABLE_NAME,
                new String[] {WordListContract.WordListColumnsEntry.GERMAN_COLUMN_NAME,WordListContract.WordListColumnsEntry.POLISH_COLUMN_NAME},
                WordListContract.WordListColumnsEntry.CATEGORY_COLUMN_NAME + " = ?",
                new String[]{category},
                null,
                null,
                null);
    }

    public static Cursor getAllWordsByArray(String[] wordList, String[] categoryNames){
        String[] wordListAndCategoryNames = joinArrayGeneric(wordList,categoryNames);
        String whereClause = "( ";
        if(wordList.length > 0) {
            for (int i = 0; i < wordList.length; i++) {
               if (i == wordList.length - 1) {
                    whereClause += WordListContract.WordListColumnsEntry.POLISH_COLUMN_NAME + " = ?";
                } else {
                    whereClause += WordListContract.WordListColumnsEntry.POLISH_COLUMN_NAME + " = ?" + " OR ";
                }
            }
            whereClause += ") AND (";
            for (int j = 0; j < wordList.length; j++) {
                if (j == wordList.length - 1) {
                    whereClause += WordListColumnsEntry.CATEGORY_COLUMN_NAME + " = ?";
                } else {
                    whereClause += WordListColumnsEntry.CATEGORY_COLUMN_NAME + " = ?" + " OR ";
                }
            }
            whereClause += " )";
            return myWordsDB.query(
                    WordListContract.WordListColumnsEntry.TABLE_NAME,
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
        final T[] result = (T[]) Array.newInstance(arrays[0].getClass().getComponentType(), length);

        int offset = 0;
        for (T[] array : arrays) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
}
