package com.example.blazej.languagelearner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.blazej.languagelearner.data.AccountListContract;
import com.example.blazej.languagelearner.data.WordAccountStatusContract;
import com.example.blazej.languagelearner.data.WordListContract;
import com.example.blazej.languagelearner.data.WordsDbHelper;

import java.util.ArrayList;


public class ReviewActivity extends AppCompatActivity {

    SharedPreferences sharedPref;
    String accountName;
    NumberPicker numberPicker;
    Cursor reviewCursor;
    ArrayList<String> wordsToReview = new ArrayList<>();
    ArrayList<String> germanWordsByAccount = new ArrayList<>();
    ArrayList<String> polishWordsByAccount = new ArrayList<>();
    private SQLiteDatabase myWordsDB;
    int wordsToReviewCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        WordsDbHelper wordsDbHelper = new WordsDbHelper(this);
        myWordsDB = wordsDbHelper.getWritableDatabase();
        numberPicker = (NumberPicker) findViewById(R.id.numberPicker2);
        sharedPref = getSharedPreferences(AccountListContract.sharedName,MODE_PRIVATE);
        accountName = sharedPref.getString(AccountListContract.sharedName,"XD");
        reviewCursor = WordAccountStatusContract.getWordAccountStatusCursorWithSpecificAccount(accountName);

        String[] wordsToReviewArray = new String[reviewCursor.getCount()];

        if(reviewCursor.getCount() > 0) {
            while (reviewCursor.moveToNext()) {
                wordsToReview.add(reviewCursor.getString(1));
            }
            wordsToReviewArray = wordsToReview.toArray(new String[wordsToReview.size()]);
        }else{
            Log.v("TAG", "reviewCursor pusty!!");
        }


        Cursor wordsToReview = getWordsByArray(wordsToReviewArray);
        if(wordsToReview.getCount() > 0) {
            while (wordsToReview.moveToNext()) {
                germanWordsByAccount.add(wordsToReview.getString(1));
                polishWordsByAccount.add(wordsToReview.getString(2));
            }
        }else{
            Log.v("TAG","Nie ma słów do powtórki!");
        }


        numberPicker.setMinValue(4);
        numberPicker.setMaxValue(reviewCursor.getCount());
    }

    public void onStartReview(View view) {
        wordsToReviewCount = numberPicker.getValue();
        Intent myIntent = new Intent(getApplicationContext(), LearningToPolishActivity.class);
        if(reviewCursor.getCount() > 3){
            myIntent.putStringArrayListExtra("german_words",germanWordsByAccount);
            myIntent.putStringArrayListExtra("polish_words",polishWordsByAccount);
            myIntent.putExtra("words_to_review_count",wordsToReviewCount);
            startActivityForResult(myIntent,1);
        }else{
            Toast.makeText(this,"Niewystarczająca ilość słów do powtórki!",Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor getWordsByArray(String[] wordList){

        String whereClause = "";
        for(int i=0; i<wordList.length; i++){
            if(i == wordList.length-1){
                whereClause += WordListContract.DatabaseColumnsEntry.POLISH_COLUMN_NAME + " = ?";
            }else {
                whereClause += WordListContract.DatabaseColumnsEntry.POLISH_COLUMN_NAME + " = ?" + " OR ";
            }
            //WordListContract.DatabaseColumnsEntry.POLISH_COLUMN_NAME + "=?"
        }
        Log.v("TAG", whereClause);
        return myWordsDB.query(
                WordListContract.DatabaseColumnsEntry.TABLE_NAME,
                null,
                whereClause,
                wordList,
                null,
                null,
                null
        );
    }
}
