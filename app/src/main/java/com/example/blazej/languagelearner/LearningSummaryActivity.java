package com.example.blazej.languagelearner;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.blazej.languagelearner.data.WordAccountStatusContract;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static android.R.id.list;

public class LearningSummaryActivity extends AppCompatActivity {
    ArrayList<String> germanWordsInCategory = new ArrayList<>();
    ArrayList<String> polishWordsInCategory = new ArrayList<>();
    ArrayList<String> learnedWords = new ArrayList<>();
    ArrayList<String> missedWords = new ArrayList<>();
    ArrayList<String> learnedWordsCategory = new ArrayList<>();
    ArrayList<String> missedWordsCategory = new ArrayList<>();
    ArrayList<String> reallyLearnedWords = new ArrayList<>();
    ArrayList<String> reallyLearnedCategories = new ArrayList<>();
    ArrayList<String> categoriesOfWordsToReview = new ArrayList<>();
    TextView ansPercent;
    //Cursor cursor;
    String categoryName;
    String accountName;
    String callingActivity;

    float badAnswers;
    float goodAnswers;
    float allAnswers;
    float goodAnsPercent;
    DecimalFormat df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_summary);
        ansPercent = (TextView)findViewById(R.id.goodAnsPercentTV);
        callingActivity = getCallingActivity().getClassName();
        Log.v("TAG", "Calling activity: " + callingActivity);
        Intent intent = getIntent();

        switch (callingActivity){
            case "com.example.blazej.languagelearner.ListenActivity":
                ///////////////////////
                Cursor myCursor1 = WordAccountStatusContract.getWordAccountStatusCursor();
                if(myCursor1.getCount() > 0){
                    while(myCursor1.moveToNext()){
                        Log.v("TAG", myCursor1.getString(1) + " -- " + myCursor1.getString(2) + " -- "  + myCursor1.getString(3) + " -- "  + myCursor1.getInt(4));
                    }
                }else{
                    Log.v("TAG", "Kursor pusty?!?!?!");
                }
                myCursor1.close();
                /////////////////////
                accountName = intent.getStringExtra("account_name");
                germanWordsInCategory = intent.getStringArrayListExtra("german_words");
                polishWordsInCategory = intent.getStringArrayListExtra("polish_words");
                categoriesOfWordsToReview = intent.getStringArrayListExtra("word_category");
                learnedWords = intent.getStringArrayListExtra("learned_words");
                missedWords = intent.getStringArrayListExtra("missed_words");
                learnedWordsCategory = intent.getStringArrayListExtra("learned_words_category");
                missedWordsCategory = intent.getStringArrayListExtra("missed_words_category");
                //categoryName = intent.getStringExtra("category_name");

                badAnswers = missedWords.size();
                goodAnswers = learnedWords.size();
                allAnswers = goodAnswers + badAnswers;
                goodAnsPercent = (goodAnswers/allAnswers)*100;
                df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.CEILING);
                Log.v("TAG","badAnswers: " + badAnswers);
                Log.v("TAG","allAnswers: " + allAnswers);
                Log.v("TAG","goodAnsPercent: " + goodAnsPercent);
                ansPercent.setText(getString(R.string.good_ans,df.format(goodAnsPercent)));
                ansPercent.setVisibility(View.VISIBLE);

                for(int i=0; i<missedWords.size();i++){
                    Log.v("TAG", "missedWords: " + missedWords.get(i));
                    Log.v("TAG", "missedWordsCategory: " + missedWordsCategory.get(i));
                }

                for(int i=0; i<learnedWords.size();i++){
                    Log.v("TAG", "learnedWords: " + learnedWords.get(i));
                    Log.v("TAG", "learnedWordsCategory: " + learnedWordsCategory.get(i));
                }


                countToFive(learnedWords,learnedWordsCategory);
                removeDuplicatesOfMissedWords(missedWords,missedWordsCategory);
                //cursor = WordAccountStatusContract.getWordAccountStatusCursor();
                if(reallyLearnedWords.size()>0) {
                    addLearnedWordsToBase(reallyLearnedWords);
                }
                if(missedWords.size()>0) {
                    addUnLearnedWordsToBase(missedWords);
                }


                Cursor cursor = WordAccountStatusContract.getWordAccountStatusCursor();
                if(cursor.getCount() > 0){
                    while(cursor.moveToNext()){
                        Log.v("TAG", cursor.getString(1) + " -- " + cursor.getString(2) + " -- "  + cursor.getString(3) + " -- "  + cursor.getInt(4));
                    }
                }else{
                    Log.v("TAG", "Kursor pusty?!?!?!");
                }
                //////////////////////////////////////
                break;

            default:
                accountName = intent.getStringExtra("account_name");
                germanWordsInCategory = intent.getStringArrayListExtra("german_words");
                polishWordsInCategory = intent.getStringArrayListExtra("polish_words");
                categoriesOfWordsToReview = intent.getStringArrayListExtra("word_category");
                learnedWords = intent.getStringArrayListExtra("learned_words");
                missedWords = intent.getStringArrayListExtra("missed_words");
                learnedWordsCategory = intent.getStringArrayListExtra("learned_words_category");
                missedWordsCategory = intent.getStringArrayListExtra("missed_words_category");
                //categoryName = intent.getStringExtra("category_name");

                badAnswers = missedWords.size();
                goodAnswers = learnedWords.size();
                allAnswers = goodAnswers + badAnswers;
                goodAnsPercent = (goodAnswers/allAnswers)*100;
                df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.CEILING);
                Log.v("TAG","badAnswers: " + badAnswers);
                Log.v("TAG","allAnswers: " + allAnswers);
                Log.v("TAG","goodAnsPercent: " + goodAnsPercent);

                ansPercent.setText(getString(R.string.good_ans,df.format(goodAnsPercent)));
                ansPercent.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    private void removeDuplicatesOfMissedWords(ArrayList<String> missedWords,ArrayList<String> missedWordsCategory){
        ArrayList<String> missedWordsTemp = new ArrayList<>();
        ArrayList<String> catOfWordsTemp = new ArrayList<>();
        Log.v("TAG", "missedWords: " + missedWords.size());

        missedWordsTemp.add(missedWords.get(0));
        catOfWordsTemp.add(missedWordsCategory.get(0));

        for(int i=0; i < missedWords.size(); i++){
            if(!missedWordsTemp.contains(missedWords.get(i))){
                missedWordsTemp.add(missedWords.get(i));
                catOfWordsTemp.add(missedWordsCategory.get(i));
            }
        }
        for(int i=0;i<missedWordsTemp.size();i++){
            Log.v("TAG","missedWordsTemp: " + missedWordsTemp.get(i));
        }
        for(int i=0;i<catOfWordsTemp.size();i++){
            Log.v("TAG","catOfWordsTemp: " + catOfWordsTemp.get(i));
        }
        this.missedWords.clear();
        this.missedWords = missedWordsTemp;
        this.missedWordsCategory.clear();
        this.missedWordsCategory = catOfWordsTemp;
    }

    private void addUnLearnedWordsToBase(ArrayList<String> missedWords){
        Log.v("TAG", "unlearnedwords size: " + missedWords.size());
        for(int i=0; i < missedWords.size(); i++) {
            if (missedWordsCategory.size() > 0){
                categoryName = missedWordsCategory.get(i);
            }
            Log.v("TAG", "Category name: " + categoryName);
            if (WordAccountStatusContract.ifWordAccountStatusCursorContain(missedWords.get(i),categoryName,accountName,1)) {
                Log.v("TAG", "Baza zawiera takie słowo, ale jest nauczone - zmieniamy na zapomniane(wtf): " + missedWords.get(i));
                ContentValues cv = new ContentValues();
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.POLISH_COLUMN_NAME, missedWords.get(i));
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME, categoryName);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_ACCOUNT_NAME, accountName);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_IS_LEARNED, 0);
                String whereClause = WordAccountStatusContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME + " = "  + "'" + categoryName+ "'"  + " AND " +
                        WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_ACCOUNT_NAME + " = " + "'" + accountName +  "'" + " AND " +
                        WordAccountStatusContract.DatabaseColumnsEntry.POLISH_COLUMN_NAME +   " = " +  "'" + missedWords.get(i) + "'";
                WordAccountStatusContract.myIsLearnedDB.update(WordAccountStatusContract.DatabaseColumnsEntry.TABLE_NAME,cv,whereClause,null);
                cv.clear();
            }else if (WordAccountStatusContract.ifWordAccountStatusCursorContain(missedWords.get(i),categoryName,accountName,0)){
                Log.v("TAG", "Baza zawiera takie nienauczone słowo: " + missedWords.get(i));
            }else if ((!WordAccountStatusContract.ifWordAccountStatusCursorContain(missedWords.get(i),categoryName,accountName,1)) ||
                    (!WordAccountStatusContract.ifWordAccountStatusCursorContain(missedWords.get(i),categoryName,accountName,0))) {
                Log.v("TAG", "Baza nie zawiera takiego słowa, dodajemy jako nienauczone: " + missedWords.get(i));
                ContentValues cv = new ContentValues();
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.POLISH_COLUMN_NAME, missedWords.get(i));
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME, categoryName);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_ACCOUNT_NAME, accountName);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_IS_LEARNED, 0);
                WordAccountStatusContract.myIsLearnedDB.insert(WordAccountStatusContract.DatabaseColumnsEntry.TABLE_NAME, null, cv);
                cv.clear();
            }
        }
    }

    private void addLearnedWordsToBase(ArrayList<String> reallyLearnedWords) {
        Log.v("TAG","reallyLearnedWords size: " + reallyLearnedWords.size());
        Log.v("TAG","reallyLearnedCategories size: " + reallyLearnedCategories.size());

        for(int i=0; i < reallyLearnedWords.size(); i++) {
            if (reallyLearnedCategories.size() > 0) {
                categoryName = reallyLearnedCategories.get(i);
            }
            Log.v("TAG", "reallyLearnedWords: " + reallyLearnedWords.get(i));
            if (WordAccountStatusContract.ifWordAccountStatusCursorContain(reallyLearnedWords.get(i),categoryName,accountName,0)) {
                Log.v("TAG", "Baza zawiera takie słowo, ale jest nienauczone: " + reallyLearnedWords.get(i));
                ContentValues cv = new ContentValues();
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.POLISH_COLUMN_NAME, reallyLearnedWords.get(i));
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME, categoryName);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_ACCOUNT_NAME, accountName);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_IS_LEARNED, 1);
                String whereClause = WordAccountStatusContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME + " = "  + "'" + categoryName+ "'"  + " AND " +
                        WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_ACCOUNT_NAME + " = " + "'" + accountName +  "'" + " AND " +
                        WordAccountStatusContract.DatabaseColumnsEntry.POLISH_COLUMN_NAME +   " = " +  "'" + reallyLearnedWords.get(i) + "'";
                WordAccountStatusContract.myIsLearnedDB.update(WordAccountStatusContract.DatabaseColumnsEntry.TABLE_NAME,cv,whereClause,null);
                cv.clear();
            }else if (WordAccountStatusContract.ifWordAccountStatusCursorContain(reallyLearnedWords.get(i),categoryName,accountName,1)){
                Log.v("TAG", "Baza zawiera takie nauczone słowo: " + reallyLearnedWords.get(i));
            }else if ((!WordAccountStatusContract.ifWordAccountStatusCursorContain(reallyLearnedWords.get(i),categoryName,accountName,1)) ||
            (!WordAccountStatusContract.ifWordAccountStatusCursorContain(reallyLearnedWords.get(i),categoryName,accountName,0))) {
                Log.v("TAG", "Baza nie zawiera takiego słowa, dodajemy jako nauczone: " + reallyLearnedWords.get(i));
                ContentValues cv = new ContentValues();
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.POLISH_COLUMN_NAME, reallyLearnedWords.get(i));
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME, categoryName);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_ACCOUNT_NAME, accountName);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_IS_LEARNED, 1);
                WordAccountStatusContract.myIsLearnedDB.insert(WordAccountStatusContract.DatabaseColumnsEntry.TABLE_NAME, null, cv);
                cv.clear();
            }
        }
    }

    private void countToFive(ArrayList<String> learnedWords,ArrayList<String> learnedWordsCategory){
        for(int i=0;i<learnedWords.size();i++){
            if(Collections.frequency(learnedWords,learnedWords.get(i)) > 4){
               if(!reallyLearnedWords.contains(learnedWords.get(i))) {
                   reallyLearnedWords.add(learnedWords.get(i));
                   reallyLearnedCategories.add(learnedWordsCategory.get(i));
               }
            }
        }

        for(int i=0;i<reallyLearnedWords.size();i++){
            Log.v("TAG", "reallyLearnedWords: " + reallyLearnedWords.get(i));
        }
        for(int i=0;i<reallyLearnedCategories.size();i++){
            Log.v("TAG", "reallyLearnedCategories: " + reallyLearnedCategories.get(i));
        }
    }

    public void nextCategory(View view) {
        Intent myIntent;
        switch(callingActivity)
        {
            case "com.example.blazej.languagelearner.LearningToPolishActivity":
                myIntent = new Intent(this, LearningToGermanActivity.class);
                break;
            case "com.example.blazej.languagelearner.LearningToGermanActivity":
                myIntent = new Intent(this, ChoosingToPolishActivity.class);
                break;
            case "com.example.blazej.languagelearner.ChoosingToPolishActivity":
                myIntent = new Intent(this, ChoosingToGermanActivity.class);
                break;
            case "com.example.blazej.languagelearner.ChoosingToGermanActivity":
                myIntent = new Intent(this, ListenActivity.class);
                break;
            case "com.example.blazej.languagelearner.ListenActivity":
                myIntent = new Intent(this, MenuActivity.class);
                break;
            default:
                myIntent = new Intent(this, MenuActivity.class);
                break;
        }

            myIntent.putStringArrayListExtra("german_words",germanWordsInCategory);
            myIntent.putStringArrayListExtra("polish_words",polishWordsInCategory);
            myIntent.putStringArrayListExtra("learned_words",learnedWords);
            myIntent.putStringArrayListExtra("missed_words",missedWords);
            myIntent.putStringArrayListExtra("learned_words_category",learnedWordsCategory);
            myIntent.putStringArrayListExtra("missed_words_category",missedWordsCategory);
            myIntent.putStringArrayListExtra("word_category",categoriesOfWordsToReview);
            myIntent.putExtra("account_name",accountName);
            startActivity(myIntent);
        }
    }

