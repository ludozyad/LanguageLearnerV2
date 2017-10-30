package com.example.blazej.languagelearner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.blazej.languagelearner.data.AccountListContract;
import com.example.blazej.languagelearner.data.WordAccountStatusContract;
import com.example.blazej.languagelearner.data.WordAccountStatusDbHelper;
import com.example.blazej.languagelearner.data.WordListContract;
import com.example.blazej.languagelearner.data.WordsDbHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;


public class MenuActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /* A constant to save and restore the Cursor that is being displayed */
    private static final String TAG = "MenuActivity";
    private static final int LOADER_ID = 1;
    private Button newContentBTN;
    private Button reviewContentBTN;

    //private SQLiteDatabase myWordsDB;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        newContentBTN = (Button)findViewById(R.id.newContentBTN);
        reviewContentBTN = (Button)findViewById(R.id.reviewContentBTN);
        newContentBTN.setVisibility(View.INVISIBLE);
        reviewContentBTN.setVisibility(View.INVISIBLE);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
        WordsDbHelper wordsDbHelper = new WordsDbHelper(this);
        WordListContract.myWordsDB = wordsDbHelper.getWritableDatabase();
        WordAccountStatusDbHelper isLearnerDbHelper = new WordAccountStatusDbHelper(this);
        WordAccountStatusContract.myIsLearnedDB = isLearnerDbHelper.getWritableDatabase();
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            @Override
            protected void onStartLoading() {

                spinner.setVisibility(View.VISIBLE);

                if (WordListContract.getAllWordsTableCursor().getCount() != 0) {
                    Log.v(TAG,"getTableCursor().getCount()!=0 -- deliverResult");
                    deliverResult(WordListContract.getAllWordsTableCursor());
                } else {
                    Log.v(TAG,"getTableCursor().getCount()==0 -- forceLoad");
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                Log.v(TAG,"loadInBackground()");
                WordListContract.headingList = DownloadTask.downloadHeadingName(WordListContract.wordListUrl, WordListContract.categoriesHtmlPlace);
                ArrayList<LinkedHashMap<String, String>> wordList = DownloadTask.downloadAllWords(WordListContract.wordListUrl, WordListContract.categoriesHtmlPlace, WordListContract.baseSiteUll, WordListContract.wordsTables);
                WordsDbHelper.fillMyBase(WordListContract.headingList, wordList, WordListContract.myWordsDB);
                return WordListContract.getAllWordsTableCursor();
            }

            @Override
            public void deliverResult(Cursor data) {
                Log.v(TAG,"deliverResult");
                super.deliverResult(WordListContract.getAllWordsTableCursor());
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(TAG,"onLoadFinished");
            spinner.setVisibility(View.GONE);
            newContentBTN.setVisibility(View.VISIBLE);
            reviewContentBTN.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(TAG,"OnLoaderReset");
    }



    public void onChooseCategoryClick(View view) {
        Intent intent = new Intent(this,ChooseCategoryActivity.class);
        startActivity(intent);
    }

    public void onReviewContent(View view) {
        Intent intent = new Intent(this, ReviewActivity.class);
        startActivity(intent);
    }
}
