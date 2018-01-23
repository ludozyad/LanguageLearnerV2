package com.example.blazej.languagelearner;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.blazej.languagelearner.data.WordAccountStatusContract;
import com.example.blazej.languagelearner.data.WordListContract;
import com.example.blazej.languagelearner.data.WordsDbHelper;


import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
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
    String categoryName;
    String accountName;
    String callingActivity;
    Cursor learnedWordsByAccount;
    float badAnswers;
    float goodAnswers;
    float allAnswers;
    float goodAnsPercent;
    DecimalFormat df;
    DateFormat dateFormat;
    String localTime;
    Date currentLocalTime;
    Calendar cal;
    ImageView howsGoinIV;
    TextView howsGoinTV;

    public static int fib(int n){
        if ((n==1)||(n==2))
            return 1;
        else
            return fib(n-1)+fib(n-2);
    }

    private String changeDate(int learnedCount, Calendar currentCal, DateFormat localDateFormat){
        int dateFromToday;
        if(learnedCount == 0){
            dateFromToday = 1;
        }else{
            dateFromToday = fib(learnedCount + 2);
        }
        currentCal.add(Calendar.DATE, dateFromToday);
        Date currentLocalTime = currentCal.getTime();
        currentCal.add(Calendar.DATE, -dateFromToday);
        return localDateFormat.format(currentLocalTime);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
        currentLocalTime = cal.getTime();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));
        localTime = dateFormat.format(currentLocalTime);
        setContentView(R.layout.activity_learning_summary);
        WordsDbHelper wordsDbHelper = new WordsDbHelper(this);
        WordListContract.myWordsDB = wordsDbHelper.getWritableDatabase();
        ansPercent = (TextView)findViewById(R.id.goodAnsPercentTV);
        callingActivity = getCallingActivity().getClassName();
        howsGoinIV = (ImageView)findViewById(R.id.howsGoingIV);
        howsGoinTV = (TextView)findViewById(R.id.howsGoingTV);
        Intent intent = getIntent();

        switch (callingActivity){
            case "com.example.blazej.languagelearner.LearningToGermanActivity":
                accountName = intent.getStringExtra("account_name");
                germanWordsInCategory = intent.getStringArrayListExtra("german_words");
                polishWordsInCategory = intent.getStringArrayListExtra("polish_words");
                categoriesOfWordsToReview = intent.getStringArrayListExtra("word_category");
                learnedWords = intent.getStringArrayListExtra("learned_words");
                missedWords = intent.getStringArrayListExtra("missed_words");
                learnedWordsCategory = intent.getStringArrayListExtra("learned_words_category");
                missedWordsCategory = intent.getStringArrayListExtra("missed_words_category");
                badAnswers = missedWords.size();
                goodAnswers = learnedWords.size();
                allAnswers = goodAnswers + badAnswers;
                goodAnsPercent = (goodAnswers/allAnswers)*100;
                df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.CEILING);
                ansPercent.setText(getString(R.string.good_ans,df.format(goodAnsPercent)));
                ansPercent.setVisibility(View.VISIBLE);

                if(germanWordsInCategory.size() < 4){

                    if(learnedWords.size()>0){
                        countToX(learnedWords,learnedWordsCategory, 2);
                    }
                    if(missedWords.size()>0) {
                        removeDuplicatesOfMissedWords(missedWords, missedWordsCategory);
                    }
                    if(reallyLearnedWords.size()>0) {
                        addLearnedWordsToBase(reallyLearnedWords);
                    }
                    if(missedWords.size()>0) {
                        addUnLearnedWordsToBase(missedWords);
                    }

                    if(missedWords.size() > 0 || reallyLearnedWords.size() > 0) {
                        addWordsToDailyBase(missedWords, reallyLearnedWords);
                    }
                    Cursor cursor1 = WordAccountStatusContract.getWordAccountStatusCursor();
                    if(cursor1.getCount() > 0){
                        while(cursor1.moveToNext()){
                            Log.v("TAG", cursor1.getString(1) + " -- " + cursor1.getString(2) + " -- "  + cursor1.getString(3) + " -- "  + cursor1.getInt(4) + " -- "  + cursor1.getInt(5) + " -- "  + cursor1.getString(6));
                        }
                    }else{
                        Log.v("TAG", "Kursor pusty");
                    }
                    Cursor cursor3 =  WordAccountStatusContract.getWordStatisticCursor();
                    if(cursor3.getCount() > 0){
                        while(cursor3.moveToNext()){
                            Log.v("TAG", "Słowo: " + cursor3.getString(1) + " -- " + cursor3.getString(5));
                        }
                    }else{
                        Log.v("TAG", "Kursor pusty");
                    }
                }
            break;
            case "com.example.blazej.languagelearner.ListenActivity":
                accountName = intent.getStringExtra("account_name");
                germanWordsInCategory = intent.getStringArrayListExtra("german_words");
                polishWordsInCategory = intent.getStringArrayListExtra("polish_words");
                categoriesOfWordsToReview = intent.getStringArrayListExtra("word_category");
                learnedWords = intent.getStringArrayListExtra("learned_words");
                missedWords = intent.getStringArrayListExtra("missed_words");
                learnedWordsCategory = intent.getStringArrayListExtra("learned_words_category");
                missedWordsCategory = intent.getStringArrayListExtra("missed_words_category");
                badAnswers = missedWords.size();
                goodAnswers = learnedWords.size();
                allAnswers = goodAnswers + badAnswers;
                goodAnsPercent = (goodAnswers/allAnswers)*100;
                df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.CEILING);
                ansPercent.setText(getString(R.string.good_ans,df.format(goodAnsPercent)));
                ansPercent.setVisibility(View.VISIBLE);

                if(learnedWords.size()>0){
                    countToX(learnedWords,learnedWordsCategory, 5);
                }
                if(missedWords.size()>0) {
                    removeDuplicatesOfMissedWords(missedWords, missedWordsCategory);
                }

                if(reallyLearnedWords.size()>0) {
                    addLearnedWordsToBase(reallyLearnedWords);
                }
                if(missedWords.size()>0) {
                    addUnLearnedWordsToBase(missedWords);
                }

                if(missedWords.size() > 0 || reallyLearnedWords.size() > 0) {
                    addWordsToDailyBase(missedWords, reallyLearnedWords);
                }

                Cursor cursor2 = WordAccountStatusContract.getWordAccountStatusCursor();
                if(cursor2.getCount() > 0){
                    while(cursor2.moveToNext()){
                        Log.v("TAG", cursor2.getString(1) + " -- " + cursor2.getString(2) + " -- "  + cursor2.getString(3) + " -- "  + cursor2.getInt(4) + " -- "  + cursor2.getInt(5) + " -- "  + cursor2.getString(6));
                    }
                }else{
                    Log.v("TAG", "Kursor pusty");
                }

                Cursor cursor4 =  WordAccountStatusContract.getWordStatisticCursor();
                if(cursor4.getCount() > 0){
                    while(cursor4.moveToNext()){
                        Log.v("TAG", "Słowo: " + cursor4.getString(1) + " -- " + cursor4.getString(5));
                    }
                }else{
                    Log.v("TAG", "Kursor pusty");
                }
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

                badAnswers = missedWords.size();
                goodAnswers = learnedWords.size();
                allAnswers = goodAnswers + badAnswers;
                goodAnsPercent = (goodAnswers/allAnswers)*100;
                df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.CEILING);
                ansPercent.setText(getString(R.string.good_ans,df.format(goodAnsPercent)));
                ansPercent.setVisibility(View.VISIBLE);

                break;
        }
       if(goodAnsPercent <30.0){
           howsGoinTV.setText("Słabo, następnym razem się popraw!");
           howsGoinIV.setImageResource(R.drawable.sad);
       }else if (goodAnsPercent >= 30.0 && goodAnsPercent <50.0){
           howsGoinTV.setText("Mogło być lepiej");
           howsGoinIV.setImageResource(R.drawable.anyway);
       }else if (goodAnsPercent >= 50.0 && goodAnsPercent <80.0){
           howsGoinTV.setText("Jest dobrze!");
           howsGoinIV.setImageResource(R.drawable.happy);
       }else if (goodAnsPercent >= 80.0){
           howsGoinTV.setText("Jesteś niesamowity, tak trzymaj!");
           howsGoinIV.setImageResource(R.drawable.lovely);
       }
    }

    private int getSpecificLearnCounterWordByAccount(String word, String categoryName, Cursor learnedWordsByAccount){
        int learnCount = 0;
        while(learnedWordsByAccount.moveToNext()){
            if(learnedWordsByAccount.getString(1).equals(word)&&learnedWordsByAccount.getString(2).equals(categoryName)){
                learnCount = learnedWordsByAccount.getInt(5);
            }
        }
        return learnCount;
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    private void removeDuplicatesOfMissedWords(ArrayList<String> missedWords,ArrayList<String> missedWordsCategory){
        ArrayList<String> missedWordsTemp = new ArrayList<>();
        ArrayList<String> catOfWordsTemp = new ArrayList<>();
        missedWordsTemp.add(missedWords.get(0));
        catOfWordsTemp.add(missedWordsCategory.get(0));
        for(int i=0; i < missedWords.size(); i++){
            if(!missedWordsTemp.contains(missedWords.get(i))){
                missedWordsTemp.add(missedWords.get(i));
                catOfWordsTemp.add(missedWordsCategory.get(i));
            }
        }
        this.missedWords.clear();
        this.missedWords = missedWordsTemp;
        this.missedWordsCategory.clear();
        this.missedWordsCategory = catOfWordsTemp;
    }

    private void addWordsToDailyBase(ArrayList<String> missedWords, ArrayList<String> learnedWords) {
        String missedCat;
        if (missedWords.size() > 0) {
            for (int i = 0; i < missedWords.size(); i++) {
                missedCat = missedWordsCategory.get(i);
                if (!WordAccountStatusContract.ifWordStatisticCursorContain(missedWords.get(i), missedCat, accountName, 0, localTime) &&
                        (!WordAccountStatusContract.ifWordStatisticCursorContain(missedWords.get(i), missedCat, accountName, 1, localTime))) {
                    ContentValues cv = new ContentValues();
                    cv.put(WordAccountStatusContract.WordStatisticColumnsEntry.POLISH_COLUMN_NAME, missedWords.get(i));
                    cv.put(WordAccountStatusContract.WordStatisticColumnsEntry.CATEGORY_COLUMN_NAME, missedCat);
                    cv.put(WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_ACCOUNT_NAME, accountName);
                    cv.put(WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_IS_LEARNED, 0);
                    cv.put(WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_LEARNED_FORGOT_DATE, localTime);
                    WordAccountStatusContract.myIsLearnedDB.insert(WordAccountStatusContract.WordStatisticColumnsEntry.TABLE_NAME, null, cv);
                    cv.clear();
                } else if (WordAccountStatusContract.ifWordStatisticCursorContain(missedWords.get(i), missedCat, accountName, 1, localTime)) {
                    ContentValues cv = new ContentValues();
                    cv.put(WordAccountStatusContract.WordStatisticColumnsEntry.POLISH_COLUMN_NAME, missedWords.get(i));
                    cv.put(WordAccountStatusContract.WordStatisticColumnsEntry.CATEGORY_COLUMN_NAME, categoryName);
                    cv.put(WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_ACCOUNT_NAME, accountName);
                    cv.put(WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_IS_LEARNED, 0);
                    cv.put(WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_LEARNED_FORGOT_DATE, localTime);
                    String whereClause = WordAccountStatusContract.WordStatisticColumnsEntry.CATEGORY_COLUMN_NAME + " = " + "'" + missedCat + "'" + " AND " +
                            WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_ACCOUNT_NAME + " = " + "'" + accountName + "'" + " AND " +
                            WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_LEARNED_FORGOT_DATE + " = " + "'" + localTime + "'" + " AND " +
                            WordAccountStatusContract.WordStatisticColumnsEntry.POLISH_COLUMN_NAME + " = " + "'" + missedWords.get(i) + "'";
                    WordAccountStatusContract.myIsLearnedDB.update(WordAccountStatusContract.WordStatisticColumnsEntry.TABLE_NAME, cv, whereClause, null);
                    cv.clear();
                } else if (WordAccountStatusContract.ifWordStatisticCursorContain(missedWords.get(i), missedCat, accountName, 0, localTime)) {

                }

            }
        }
        String learnedCat;
        if (learnedWords.size() > 0) {
            for (int i = 0; i < learnedWords.size(); i++) {
                learnedCat = reallyLearnedCategories.get(i);
                if (!WordAccountStatusContract.ifWordStatisticCursorContain(learnedWords.get(i), learnedCat, accountName, 0, localTime) &&
                        (!WordAccountStatusContract.ifWordStatisticCursorContain(learnedWords.get(i), learnedCat, accountName, 1, localTime))) {
                    ContentValues cv = new ContentValues();
                    cv.put(WordAccountStatusContract.WordStatisticColumnsEntry.POLISH_COLUMN_NAME, learnedWords.get(i));
                    cv.put(WordAccountStatusContract.WordStatisticColumnsEntry.CATEGORY_COLUMN_NAME, learnedCat);
                    cv.put(WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_ACCOUNT_NAME, accountName);
                    cv.put(WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_IS_LEARNED, 1);
                    cv.put(WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_LEARNED_FORGOT_DATE, localTime);
                    WordAccountStatusContract.myIsLearnedDB.insert(WordAccountStatusContract.WordStatisticColumnsEntry.TABLE_NAME, null, cv);
                    cv.clear();
                } else if (WordAccountStatusContract.ifWordStatisticCursorContain(learnedWords.get(i), learnedCat, accountName, 0, localTime)) {
                    ContentValues cv = new ContentValues();
                    cv.put(WordAccountStatusContract.WordStatisticColumnsEntry.POLISH_COLUMN_NAME, learnedWords.get(i));
                    cv.put(WordAccountStatusContract.WordStatisticColumnsEntry.CATEGORY_COLUMN_NAME, learnedCat);
                    cv.put(WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_ACCOUNT_NAME, accountName);
                    cv.put(WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_IS_LEARNED, 1);
                    cv.put(WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_LEARNED_FORGOT_DATE, localTime);
                    String whereClause = WordAccountStatusContract.WordStatisticColumnsEntry.CATEGORY_COLUMN_NAME + " = " + "'" + learnedCat + "'" + " AND " +
                            WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_ACCOUNT_NAME + " = " + "'" + accountName + "'" + " AND " +
                            WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_LEARNED_FORGOT_DATE + " = " + "'" + localTime + "'" + " AND " +
                            WordAccountStatusContract.WordStatisticColumnsEntry.POLISH_COLUMN_NAME + " = " + "'" + learnedWords.get(i) + "'";
                    WordAccountStatusContract.myIsLearnedDB.update(WordAccountStatusContract.WordStatisticColumnsEntry.TABLE_NAME, cv, whereClause, null);
                    cv.clear();
                } else if (WordAccountStatusContract.ifWordStatisticCursorContain(learnedWords.get(i), learnedCat, accountName, 1, localTime)) {

                }
            }
        }
    }

    private void addUnLearnedWordsToBase(ArrayList<String> missedWords){
        for(int i=0; i < missedWords.size(); i++) {
            if (missedWordsCategory.size() > 0){
                categoryName = missedWordsCategory.get(i);
            }
            if ((WordAccountStatusContract.ifWordAccountStatusCursorContain(missedWords.get(i),categoryName,accountName,1)) ||
            (WordAccountStatusContract.ifWordAccountStatusCursorContain(missedWords.get(i),categoryName,accountName,0))){
                ContentValues cv = new ContentValues();
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.POLISH_COLUMN_NAME, missedWords.get(i));
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.CATEGORY_COLUMN_NAME, categoryName);
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_ACCOUNT_NAME, accountName);
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_IS_LEARNED, 0);
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_IS_LEARNED_COUNTER, 0);
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_REVIEW_DATE, changeDate(0,cal,dateFormat));
                String whereClause = WordAccountStatusContract.WordAccountStatusColumnsEntry.CATEGORY_COLUMN_NAME + " = "  + "'" + categoryName+ "'"  + " AND " +
                        WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_ACCOUNT_NAME + " = " + "'" + accountName +  "'" + " AND " +
                        WordAccountStatusContract.WordAccountStatusColumnsEntry.POLISH_COLUMN_NAME +   " = " +  "'" + missedWords.get(i) + "'";
                WordAccountStatusContract.myIsLearnedDB.update(WordAccountStatusContract.WordAccountStatusColumnsEntry.TABLE_NAME,cv,whereClause,null);
                cv.clear();
            }else if ((!WordAccountStatusContract.ifWordAccountStatusCursorContain(missedWords.get(i),categoryName,accountName,1)) ||
                    (!WordAccountStatusContract.ifWordAccountStatusCursorContain(missedWords.get(i),categoryName,accountName,0))) {
                ContentValues cv = new ContentValues();
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.POLISH_COLUMN_NAME, missedWords.get(i));
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.CATEGORY_COLUMN_NAME, categoryName);
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_ACCOUNT_NAME, accountName);
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_IS_LEARNED, 0);
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_IS_LEARNED_COUNTER, 0);
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_REVIEW_DATE, changeDate(0,cal,dateFormat));
                WordAccountStatusContract.myIsLearnedDB.insert(WordAccountStatusContract.WordAccountStatusColumnsEntry.TABLE_NAME, null, cv);
                cv.clear();
            }
        }
    }

    private void addLearnedWordsToBase(ArrayList<String> reallyLearnedWords) {
        for(int i=0; i < reallyLearnedWords.size(); i++) {
            if (reallyLearnedCategories.size() > 0) {
                categoryName = reallyLearnedCategories.get(i);
            }
            if (WordAccountStatusContract.ifWordAccountStatusCursorContain(reallyLearnedWords.get(i),categoryName,accountName,0)) {
                ContentValues cv = new ContentValues();
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.POLISH_COLUMN_NAME, reallyLearnedWords.get(i));
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.CATEGORY_COLUMN_NAME, categoryName);
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_ACCOUNT_NAME, accountName);
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_IS_LEARNED, 1);
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_IS_LEARNED_COUNTER, 1);
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_REVIEW_DATE, changeDate(1,cal,dateFormat));
                String whereClause = WordAccountStatusContract.WordAccountStatusColumnsEntry.CATEGORY_COLUMN_NAME + " = "  + "'" + categoryName+ "'"  + " AND " +
                        WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_ACCOUNT_NAME + " = " + "'" + accountName +  "'" + " AND " +
                        WordAccountStatusContract.WordAccountStatusColumnsEntry.POLISH_COLUMN_NAME +   " = " +  "'" + reallyLearnedWords.get(i) + "'";
                WordAccountStatusContract.myIsLearnedDB.update(WordAccountStatusContract.WordAccountStatusColumnsEntry.TABLE_NAME,cv,whereClause,null);
                cv.clear();
            }else if (WordAccountStatusContract.ifWordAccountStatusCursorContain(reallyLearnedWords.get(i),categoryName,accountName,1)){
                learnedWordsByAccount = WordAccountStatusContract.getWordAccountStatusCursorWithSpecificAccountLearned(accountName);
                int learnedCount = getSpecificLearnCounterWordByAccount(reallyLearnedWords.get(i),categoryName,learnedWordsByAccount);
                ContentValues cv = new ContentValues();
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.POLISH_COLUMN_NAME, reallyLearnedWords.get(i));
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.CATEGORY_COLUMN_NAME, categoryName);
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_ACCOUNT_NAME, accountName);
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_IS_LEARNED, 1);
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_IS_LEARNED_COUNTER, learnedCount + 1);
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_REVIEW_DATE, changeDate(learnedCount+1,cal,dateFormat));
                String whereClause = WordAccountStatusContract.WordAccountStatusColumnsEntry.CATEGORY_COLUMN_NAME + " = "  + "'" + categoryName+ "'"  + " AND " +
                        WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_ACCOUNT_NAME + " = " + "'" + accountName +  "'" + " AND " +
                        WordAccountStatusContract.WordAccountStatusColumnsEntry.POLISH_COLUMN_NAME +   " = " +  "'" + reallyLearnedWords.get(i) + "'";
                WordAccountStatusContract.myIsLearnedDB.update(WordAccountStatusContract.WordAccountStatusColumnsEntry.TABLE_NAME,cv,whereClause,null);
                cv.clear();
                learnedWordsByAccount.close();
            }else if ((!WordAccountStatusContract.ifWordAccountStatusCursorContain(reallyLearnedWords.get(i),categoryName,accountName,1)) ||
            (!WordAccountStatusContract.ifWordAccountStatusCursorContain(reallyLearnedWords.get(i),categoryName,accountName,0))) {
                ContentValues cv = new ContentValues();
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.POLISH_COLUMN_NAME, reallyLearnedWords.get(i));
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.CATEGORY_COLUMN_NAME, categoryName);
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_ACCOUNT_NAME, accountName);
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_IS_LEARNED, 1);
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_IS_LEARNED_COUNTER, 1);
                cv.put(WordAccountStatusContract.WordAccountStatusColumnsEntry.COLUMN_REVIEW_DATE, changeDate(1,cal,dateFormat));
                WordAccountStatusContract.myIsLearnedDB.insert(WordAccountStatusContract.WordAccountStatusColumnsEntry.TABLE_NAME, null, cv);
                cv.clear();
            }
        }
    }

    private void countToX(ArrayList<String> learnedWords,ArrayList<String> learnedWordsCategory,int counter){
        for(int i=0;i<learnedWords.size();i++){
            if(Collections.frequency(learnedWords,learnedWords.get(i)) > counter - 1){
               if(!reallyLearnedWords.contains(learnedWords.get(i))) {
                   reallyLearnedWords.add(learnedWords.get(i));
                   reallyLearnedCategories.add(learnedWordsCategory.get(i));
               }
            }
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
                if(germanWordsInCategory.size() > 3) {
                    myIntent = new Intent(this, ChoosingToPolishActivity.class);
                    break;
                }else{
                    myIntent = new Intent(this, MenuActivity.class);
                    break;
                }
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

