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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.blazej.languagelearner.data.AccountListContract;
import com.example.blazej.languagelearner.data.WordListContract;
import com.example.blazej.languagelearner.data.WordsDbHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;


public class MenuActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /* A constant to save and restore the Cursor that is being displayed */
    private static final String TAG = "MenuActivity";
    private static final int LOADER_ID = 1;

    private SQLiteDatabase myDB;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
        WordsDbHelper dbHelper = new WordsDbHelper(this);
        myDB = dbHelper.getWritableDatabase();
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            @Override
            protected void onStartLoading() {

                spinner.setVisibility(View.VISIBLE);

                if (getTableCursor().getCount() != 0) {
                    Log.v(TAG,"getTableCursor().getCount()!=0 -- deliverResult");
                    deliverResult(getTableCursor());
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
                WordsDbHelper.fillMyBase(WordListContract.headingList, wordList, myDB);
                return getTableCursor();
            }

            @Override
            public void deliverResult(Cursor data) {
                Log.v(TAG,"deliverResult");
                super.deliverResult(getTableCursor());
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(TAG,"onLoadFinished");
        Cursor myCursor = getTableCursor();
        while(myCursor.moveToNext()){
            int id = myCursor.getInt(0);
            String germanWord = myCursor.getString(1);
            String polishWord = myCursor.getString(2);
            String category = myCursor.getString(3);
            int catCount = myCursor.getInt(4);
            Log.v(TAG,id + " " + germanWord + " " + polishWord + " " + category + " " + catCount);
            spinner.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(TAG,"OnLoaderReset");
    }

    public Cursor getTableCursor() {
        Log.v(TAG,"getTableCursor()");
        return this.myDB.query(
                WordListContract.DatabaseColumnsEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WordListContract.DatabaseColumnsEntry._ID
        );
    }

    public void onChooseCategoryClick(View view) {
        Intent intent = new Intent(this,ChooseCategoryActivity.class);
        startActivity(intent);
    }
}
