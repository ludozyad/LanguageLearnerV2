package com.example.blazej.languagelearner.data;

import android.provider.BaseColumns;

/**
 * Created by Blazej on 22.10.2017.
 */

public class WordAccountStatusContract {
    public static final class DatabaseColumnsEntry implements BaseColumns {
        public static final String TABLE_NAME = "word_account_status";
        public static final String GERMAN_COLUMN_NAME = "german_column";
        public static final String POLISH_COLUMN_NAME = "polish_column";
        public static final String COLUMN_ACCOUNT_NAME = "name_column";
        public static final String COLUMN_IS_LEARNED = "is_learned_column";
    }
}
