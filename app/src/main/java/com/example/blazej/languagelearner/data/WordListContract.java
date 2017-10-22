package com.example.blazej.languagelearner.data;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

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


    public static final class DatabaseColumnsEntry implements BaseColumns {
        public static final String TABLE_NAME = "words";
        public static final String GERMAN_COLUMN_NAME = "german_column";
        public static final String POLISH_COLUMN_NAME = "polish_column";
        public static final String CATEGORY_COLUMN_NAME = "title_column";
        public static final String CATEGORY_COUNT_COLUMN_NAME = "category_count";
    }
}
