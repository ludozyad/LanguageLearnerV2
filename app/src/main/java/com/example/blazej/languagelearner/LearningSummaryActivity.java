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
import android.widget.TextView;

import com.example.blazej.languagelearner.data.WordAccountStatusContract;
import com.example.blazej.languagelearner.data.WordListContract;
import com.example.blazej.languagelearner.data.WordsDbHelper;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

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
    //ArrayList<Entry> yvalues = new ArrayList<>();
    //PieDataSet dataSet;
    //Legend legend = new Legend();
    //PieData data = new PieData();
    //ArrayList<String> xVals = new ArrayList<>();
    TextView ansPercent;
    //Cursor cursor;
    String categoryName;
    String accountName;
    String callingActivity;
    Cursor learnedWordsByAccount;
    float badAnswers;
    float goodAnswers;
    float allAnswers;
    float goodAnsPercent;
    DecimalFormat df;
    //PieChart pieChart;
    DateFormat dateFormat;
    String localTime;
    Date currentLocalTime;
    Calendar cal;

    public static int fib(int n){
        if ((n==1)||(n==2))
            return 1;
        else
            return fib(n-1)+fib(n-2);
    }

    private void changeDate(int learnedCount){
        int dateFromToday;
        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
        if(learnedCount == 0){
            dateFromToday = 1;
        }else{
            dateFromToday = fib(learnedCount + 2);
        }
        Log.v("TAG","learnedCount: " + learnedCount);
        Log.v("TAG","dateFromToday: " + dateFromToday);
        cal.add(Calendar.DATE, dateFromToday);
        currentLocalTime = cal.getTime();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));
        localTime = dateFormat.format(currentLocalTime);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
        //cal.add(Calendar.DATE, 120);
        currentLocalTime = cal.getTime();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));
        localTime = dateFormat.format(currentLocalTime);

        setContentView(R.layout.activity_learning_summary);
        WordsDbHelper wordsDbHelper = new WordsDbHelper(this);
        WordListContract.myWordsDB = wordsDbHelper.getWritableDatabase();
        //pieChart = (PieChart) findViewById(R.id.pieChart);
        //pieChart.setUsePercentValues(true);
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

                /*
                PieDataSet dataSetLA = new PieDataSet(yvalues, "Wyniki Nauki");
                Legend legendLA = new Legend();
                PieData dataLA = new PieData();
                ArrayList<String> xValsLA = new ArrayList<>();
                ArrayList<Entry> yvaluesLA = new ArrayList<>();

                yvaluesLA.add(new Entry(badAnswers, 0));
                yvaluesLA.add(new Entry(goodAnswers, 1));
                Log.v("TAG","yvalues: " + yvalues.size());
                xValsLA.add("Błędne Odpowiedzi");
                xValsLA.add("Poprawne Odpowiedzi");
                legendLA.setEnabled(false);
                dataLA.setValueFormatter(new PercentFormatter());
                dataLA.setValueTextSize(22f);
                dataLA.setValueTextColor(Color.DKGRAY);
                dataLA = new PieData(xValsLA, dataSetLA);
                pieChart.setData(dataLA);
                dataSetLA.setColors(ColorTemplate.JOYFUL_COLORS);
                pieChart.setDrawHoleEnabled(false);
                pieChart.setScaleX(0.6f);
                pieChart.setScaleY(0.6f);
                pieChart.setDescription("");
                */
                /*
                yvalues.add(new Entry(badAnswers, 0));
                yvalues.add(new Entry(goodAnswers, 1));
                dataSet = new PieDataSet(yvalues, "Wyniki Nauki");
                Log.v("TAG","yvalues: " + yvalues.size());
                xVals.add("Błędne Odpowiedzi");
                xVals.add("Poprawne Odpowiedzi");
                legend = pieChart.getLegend();
                legend.setEnabled(false);
                data = new PieData(xVals, dataSet);
                data.setValueFormatter(new PercentFormatter());
                data.setValueTextSize(22f);
                data.setValueTextColor(Color.DKGRAY);
                pieChart.setData(data);
                dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                pieChart.setDrawHoleEnabled(false);
                pieChart.setScaleX(0.6f);
                pieChart.setScaleY(0.6f);
                pieChart.setDescription("");
*/
                for(int i=0; i<missedWords.size();i++){
                    Log.v("TAG", "missedWords: " + missedWords.get(i));
                    Log.v("TAG", "missedWordsCategory: " + missedWordsCategory.get(i));
                }

                for(int i=0; i<learnedWords.size();i++){
                    Log.v("TAG", "learnedWords: " + learnedWords.get(i));
                    Log.v("TAG", "learnedWordsCategory: " + learnedWordsCategory.get(i));
                }

                if(learnedWords.size()>0){
                    countToFive(learnedWords,learnedWordsCategory);
                }
                if(missedWords.size()>0) {
                    removeDuplicatesOfMissedWords(missedWords, missedWordsCategory);
                }
                //cursor = WordAccountStatusContract.getWordAccountStatusCursor();

                //learnedWordsByAccount = WordAccountStatusContract.getWordAccountStatusCursorWithSpecificAccountLearned(accountName);
                //Log.v("TAG","learnedWordsByAccount(cursor) size: " + learnedWordsByAccount.getCount());

                if(reallyLearnedWords.size()>0) {
                    addLearnedWordsToBase(reallyLearnedWords);
                }
                if(missedWords.size()>0) {
                    addUnLearnedWordsToBase(missedWords);
                }


                Cursor cursor = WordAccountStatusContract.getWordAccountStatusCursor();
                if(cursor.getCount() > 0){
                    while(cursor.moveToNext()){
                        Log.v("TAG", cursor.getString(1) + " -- " + cursor.getString(2) + " -- "  + cursor.getString(3) + " -- "  + cursor.getInt(4) + " -- "  + cursor.getInt(5) + " -- "  + cursor.getString(6));
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
/*
                yvalues.add(new Entry(badAnswers, 0));
                yvalues.add(new Entry(goodAnswers, 1));
                dataSet = new PieDataSet(yvalues, "Wyniki Nauki");
                Log.v("TAG","yvalues: " + yvalues.size());
                xVals.add("Błędne Odpowiedzi");
                xVals.add("Poprawne Odpowiedzi");
                legend = pieChart.getLegend();
                legend.setEnabled(false);
                data = new PieData(xVals, dataSet);
                data.setValueFormatter(new PercentFormatter());
                data.setValueTextSize(22f);
                data.setValueTextColor(Color.DKGRAY);
                pieChart.setData(data);
                dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                pieChart.setDrawHoleEnabled(false);
                pieChart.setScaleX(0.6f);
                pieChart.setScaleY(0.6f);
                pieChart.setDescription("");
                */
                break;
        }
    }

    private int getSpecificLearnCounterWordByAccount(String word, String categoryName, Cursor learnedWordsByAccount){
        int learnCount = 0;
        Log.v("TAG","z getSpecificLearnCounterWordByAccount learnedWordsByAccount count: " + learnedWordsByAccount.getCount());
        while(learnedWordsByAccount.moveToNext()){
            if(learnedWordsByAccount.getString(1).equals(word)&&learnedWordsByAccount.getString(2).equals(categoryName)){
                learnCount = learnedWordsByAccount.getInt(5);
                Log.v("TAG", "Znaleziono słowo, learnCount to: " + learnCount);
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
            changeDate(0);
            Log.v("TAG", "Category name: " + categoryName);
            if (WordAccountStatusContract.ifWordAccountStatusCursorContain(missedWords.get(i),categoryName,accountName,1)) {
                Log.v("TAG", "Baza zawiera takie słowo, ale jest nauczone - zmieniamy na zapomniane(wtf): " + missedWords.get(i));
                ContentValues cv = new ContentValues();
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.POLISH_COLUMN_NAME, missedWords.get(i));
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME, categoryName);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_ACCOUNT_NAME, accountName);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_IS_LEARNED, 0);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_IS_LEARNED_COUNTER, 0);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_REVIEW_DATE, localTime);
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
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_IS_LEARNED_COUNTER, 0);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_REVIEW_DATE, localTime);
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
                changeDate(1);
                ContentValues cv = new ContentValues();
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.POLISH_COLUMN_NAME, reallyLearnedWords.get(i));
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME, categoryName);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_ACCOUNT_NAME, accountName);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_IS_LEARNED, 1);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_IS_LEARNED_COUNTER, 1);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_REVIEW_DATE, localTime);
                String whereClause = WordAccountStatusContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME + " = "  + "'" + categoryName+ "'"  + " AND " +
                        WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_ACCOUNT_NAME + " = " + "'" + accountName +  "'" + " AND " +
                        WordAccountStatusContract.DatabaseColumnsEntry.POLISH_COLUMN_NAME +   " = " +  "'" + reallyLearnedWords.get(i) + "'";
                WordAccountStatusContract.myIsLearnedDB.update(WordAccountStatusContract.DatabaseColumnsEntry.TABLE_NAME,cv,whereClause,null);
                cv.clear();
            }else if (WordAccountStatusContract.ifWordAccountStatusCursorContain(reallyLearnedWords.get(i),categoryName,accountName,1)){
                Log.v("TAG", "Baza zawiera takie nauczone słowo: " + reallyLearnedWords.get(i));
                learnedWordsByAccount = WordAccountStatusContract.getWordAccountStatusCursorWithSpecificAccountLearned(accountName);
                int learnedCount = getSpecificLearnCounterWordByAccount(reallyLearnedWords.get(i),categoryName,learnedWordsByAccount);
                changeDate(learnedCount+1);
                Log.v("TAG", "Liczba nauczen slowa: " + learnedCount);
                ContentValues cv = new ContentValues();
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.POLISH_COLUMN_NAME, reallyLearnedWords.get(i));
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME, categoryName);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_ACCOUNT_NAME, accountName);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_IS_LEARNED, 1);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_IS_LEARNED_COUNTER, learnedCount + 1);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_REVIEW_DATE, localTime);
                String whereClause = WordAccountStatusContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME + " = "  + "'" + categoryName+ "'"  + " AND " +
                        WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_ACCOUNT_NAME + " = " + "'" + accountName +  "'" + " AND " +
                        WordAccountStatusContract.DatabaseColumnsEntry.POLISH_COLUMN_NAME +   " = " +  "'" + reallyLearnedWords.get(i) + "'";
                WordAccountStatusContract.myIsLearnedDB.update(WordAccountStatusContract.DatabaseColumnsEntry.TABLE_NAME,cv,whereClause,null);
                cv.clear();
                learnedWordsByAccount.close();
            }else if ((!WordAccountStatusContract.ifWordAccountStatusCursorContain(reallyLearnedWords.get(i),categoryName,accountName,1)) ||
            (!WordAccountStatusContract.ifWordAccountStatusCursorContain(reallyLearnedWords.get(i),categoryName,accountName,0))) {
                Log.v("TAG", "Baza nie zawiera takiego słowa, dodajemy jako nauczone: " + reallyLearnedWords.get(i));
                changeDate(1);
                ContentValues cv = new ContentValues();
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.POLISH_COLUMN_NAME, reallyLearnedWords.get(i));
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME, categoryName);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_ACCOUNT_NAME, accountName);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_IS_LEARNED, 1);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_IS_LEARNED_COUNTER, 1);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_REVIEW_DATE, localTime);
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

