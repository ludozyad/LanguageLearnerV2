package com.example.blazej.languagelearner;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.blazej.languagelearner.data.WordListContract;
import com.example.blazej.languagelearner.data.WordsDbHelper;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MenuActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String TAG = "MenuActivity";
    private static final int LOADER_ID = 1;
    private Button newContentBTN;
    private Button statisticBTN;
    private Button reviewContentBTN;
    private Button learnGrammarBTN;
    private Button retryBTN;
    private TextView pleaseWaitTV;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        statisticBTN = (Button)findViewById(R.id.checkStatisticsBTN);
        newContentBTN = (Button)findViewById(R.id.newContentBTN);
        reviewContentBTN = (Button)findViewById(R.id.reviewContentBTN);
        learnGrammarBTN = (Button)findViewById(R.id.learnGrammarBTN);
        retryBTN = (Button)findViewById(R.id.retryBTN);
        pleaseWaitTV = (TextView)findViewById(R.id.pleaseWaitTV);
        newContentBTN.setVisibility(View.INVISIBLE);
        reviewContentBTN.setVisibility(View.INVISIBLE);
        learnGrammarBTN.setVisibility(View.INVISIBLE);
        statisticBTN.setVisibility(View.INVISIBLE);
        retryBTN.setVisibility(View.INVISIBLE);
        pleaseWaitTV.setVisibility(View.INVISIBLE);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
        WordsDbHelper wordsDbHelper = new WordsDbHelper(this);
        WordListContract.myWordsDB = wordsDbHelper.getWritableDatabase();
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        retryBTN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               onClickRestart();
            }
        });

    }
    public void onClickRestart(){
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            @Override
            protected void onStartLoading() {
                spinner.setVisibility(View.VISIBLE);
                pleaseWaitTV.setVisibility(View.VISIBLE);
                retryBTN.setVisibility(View.INVISIBLE);
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
                if(WordListContract.headingList!=null && wordList!=null) {
                    WordsDbHelper.fillMyBase(WordListContract.headingList, wordList, WordListContract.myWordsDB);
                    return WordListContract.getAllWordsTableCursor();
                }else{
                    Log.v("TAG","Internet Error!");
                    Log.v("TAG", "WordListContract.getAllWordsTableCursor().size: " + WordListContract.getAllWordsTableCursor().getCount());
                    return WordListContract.getAllWordsTableCursor();
                }
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
        pleaseWaitTV.setVisibility(View.INVISIBLE);
        if(data.getCount() > 0) {
            newContentBTN.setVisibility(View.VISIBLE);
            reviewContentBTN.setVisibility(View.VISIBLE);
            learnGrammarBTN.setVisibility(View.VISIBLE);
            statisticBTN.setVisibility(View.VISIBLE);
            retryBTN.setVisibility(View.INVISIBLE);
        }else{
            Toast.makeText(this,"Brak połączenia z internetem",Toast.LENGTH_SHORT).show();
            retryBTN.setVisibility(View.VISIBLE);
        }
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

    public void onGrammarLearn(View view) {
        Intent intent = new Intent(this, ChooseGrammarCategoryActivity.class);
        startActivity(intent);
    }

    public void onStatisticView(View view) {
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

    public void showAppInfo(View view) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Informacje o aplikacji")
                .setMessage(R.string.about)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

}
