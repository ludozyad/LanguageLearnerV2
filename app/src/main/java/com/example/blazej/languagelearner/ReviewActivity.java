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
    ArrayList<String> categoriesOfWordsToReview = new ArrayList<>();
    int wordsToReviewCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        WordsDbHelper wordsDbHelper = new WordsDbHelper(this);
        WordListContract.myWordsDB = wordsDbHelper.getWritableDatabase();
        numberPicker = (NumberPicker) findViewById(R.id.numberPicker2);
        sharedPref = getSharedPreferences(AccountListContract.sharedName,MODE_PRIVATE);
        accountName = sharedPref.getString(AccountListContract.sharedName,"XD");
        reviewCursor = WordAccountStatusContract.getWordAccountStatusCursorWithSpecificAccount(accountName);

        String[] wordsToReviewArray = new String[reviewCursor.getCount()];
        String[] categoriesOfWordsToReviewArray = new String[reviewCursor.getCount()];
        if(reviewCursor.getCount() > 0) {
            while (reviewCursor.moveToNext()) {
                wordsToReview.add(reviewCursor.getString(1));
                categoriesOfWordsToReview.add(reviewCursor.getString(2));
            }
            wordsToReviewArray = wordsToReview.toArray(new String[wordsToReview.size()]);
            categoriesOfWordsToReviewArray = categoriesOfWordsToReview.toArray(new String[categoriesOfWordsToReview.size()]);
        }else{
            Log.v("TAG", "reviewCursor pusty!!");
        }

        for(int i=0; i<categoriesOfWordsToReview.size(); i++){
            Log.v("TAG","categoriesOfWordsToReview: " + categoriesOfWordsToReview.get(i));
        }

        Cursor wordsToReview = WordListContract.getAllWordsByArray(wordsToReviewArray,categoriesOfWordsToReviewArray);
        if(wordsToReview.getCount() > 0) {
            while (wordsToReview.moveToNext()) {
                Log.v("TAG","0: " + wordsToReview.getString(0));
                Log.v("TAG","1: " + wordsToReview.getString(1));
                Log.v("TAG","2: " + wordsToReview.getString(2));
                Log.v("TAG","3: " + wordsToReview.getString(3));
                Log.v("TAG","4: " + wordsToReview.getString(4));
                germanWordsByAccount.add(wordsToReview.getString(1));
                polishWordsByAccount.add(wordsToReview.getString(2));
            }
        }else{
            Log.v("TAG","Nie ma słów do powtórki!");
        }
        wordsToReview.close();
        numberPicker.setMinValue(4);
        numberPicker.setMaxValue(reviewCursor.getCount());
    }

    public void onStartReview(View view) {
        wordsToReviewCount = numberPicker.getValue();
        Intent myIntent = new Intent(getApplicationContext(), LearningToPolishActivity.class);
        if(reviewCursor.getCount() > 3){
            myIntent.putStringArrayListExtra("german_words",germanWordsByAccount);
            myIntent.putStringArrayListExtra("polish_words",polishWordsByAccount);
            myIntent.putStringArrayListExtra("word_category",categoriesOfWordsToReview);
            myIntent.putExtra("words_to_review_count",wordsToReviewCount);
            startActivityForResult(myIntent,1);
        }else{
            Toast.makeText(this,"Niewystarczająca ilość słów do powtórki!",Toast.LENGTH_SHORT).show();
        }
    }


}
