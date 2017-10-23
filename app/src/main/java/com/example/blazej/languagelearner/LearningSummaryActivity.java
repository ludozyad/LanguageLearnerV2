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

import com.example.blazej.languagelearner.data.WordAccountStatusContract;

import java.util.ArrayList;
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
    ArrayList<String> reallyLearnedWords = new ArrayList<>();
    //Cursor cursor;
    String categoryName;
    String accountName;
    String callingActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_summary);
        callingActivity = getCallingActivity().getClassName();
        Log.v("TAG", "Calling activity: " + callingActivity);
        Intent intent = getIntent();

        switch (callingActivity){
            case "com.example.blazej.languagelearner.LearningToGermanActivity":

                accountName = intent.getStringExtra("account_name");
                germanWordsInCategory = intent.getStringArrayListExtra("german_words");
                polishWordsInCategory = intent.getStringArrayListExtra("polish_words");
                learnedWords = intent.getStringArrayListExtra("learned_words");
                missedWords = intent.getStringArrayListExtra("missed_words");
                categoryName = intent.getStringExtra("category_name");

                break;
            case "com.example.blazej.languagelearner.LearningToPolishActivity":
                accountName = intent.getStringExtra("account_name");
                germanWordsInCategory = intent.getStringArrayListExtra("german_words");
                polishWordsInCategory = intent.getStringArrayListExtra("polish_words");
                learnedWords = intent.getStringArrayListExtra("learned_words");
                missedWords = intent.getStringArrayListExtra("missed_words");
                categoryName = intent.getStringExtra("category_name");

                break;
            case "com.example.blazej.languagelearner.ChoosingToPolishActivity":
                accountName = intent.getStringExtra("account_name");
                germanWordsInCategory = intent.getStringArrayListExtra("german_words");
                polishWordsInCategory = intent.getStringArrayListExtra("polish_words");
                learnedWords = intent.getStringArrayListExtra("learned_words");
                missedWords = intent.getStringArrayListExtra("missed_words");
                categoryName = intent.getStringExtra("category_name");

                break;
            case "com.example.blazej.languagelearner.ChoosingToGermanActivity":
                accountName = intent.getStringExtra("account_name");
                germanWordsInCategory = intent.getStringArrayListExtra("german_words");
                polishWordsInCategory = intent.getStringArrayListExtra("polish_words");
                learnedWords = intent.getStringArrayListExtra("learned_words");
                missedWords = intent.getStringArrayListExtra("missed_words");
                categoryName = intent.getStringExtra("category_name");

                break;
            case "com.example.blazej.languagelearner.ListenActivity":
                accountName = intent.getStringExtra("account_name");
                germanWordsInCategory = intent.getStringArrayListExtra("german_words");
                polishWordsInCategory = intent.getStringArrayListExtra("polish_words");
                learnedWords = intent.getStringArrayListExtra("learned_words");
                missedWords = intent.getStringArrayListExtra("missed_words");
                categoryName = intent.getStringExtra("category_name");

                reallyLearnedWords = countToFive(learnedWords);
                //cursor = WordAccountStatusContract.getWordAccountStatusCursor();
                if(reallyLearnedWords.size()>0) {
                    addLearnedWordsToBase(reallyLearnedWords);
                }
                ArrayList<String> missedWordsTemp = new ArrayList<>();
                if(missedWords.size() > 0){
                    Set<String> hs = new HashSet<>();
                    hs.addAll(missedWords);
                    missedWordsTemp.clear();
                    missedWordsTemp.addAll(hs);
                    addUnLearnedWordsToBase(missedWordsTemp);
                }
                Cursor cursor = WordAccountStatusContract.getWordAccountStatusCursor();
                if(cursor.getCount() > 0){
                    while(cursor.moveToNext()){
                        Log.v("TAG", cursor.getString(1) + " -- " + cursor.getString(2) + " -- "  + cursor.getString(3) + " -- "  + cursor.getInt(4));
                    }
                }else{
                    Log.v("TAG", "Kursor pusty?!?!?!");
                }
                break;
        }

    }

    private void addUnLearnedWordsToBase(ArrayList<String> reallyUnLearnedWords){
        Log.v("TAG", "unlearnedwords size: " + reallyUnLearnedWords.size());
        for(int i=0; i < reallyUnLearnedWords.size(); i++) {
            if (!WordAccountStatusContract.ifWordAccountStatusCursorContain(reallyUnLearnedWords.get(i),categoryName,accountName)) {
                ContentValues cv = new ContentValues();
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.POLISH_COLUMN_NAME, reallyUnLearnedWords.get(i));
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME, categoryName);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_ACCOUNT_NAME, accountName);
                cv.put(WordAccountStatusContract.DatabaseColumnsEntry.COLUMN_IS_LEARNED, 0);
                WordAccountStatusContract.myIsLearnedDB.insert(WordAccountStatusContract.DatabaseColumnsEntry.TABLE_NAME, null, cv);
                cv.clear();
            }else{
                Log.v("TAG", "znalezione słowo posiada już duplikat: " + reallyUnLearnedWords.get(i));
            }
        }
    }

    private void addLearnedWordsToBase(ArrayList<String> reallyLearnedWords) {
        for(int i=0; i < reallyLearnedWords.size(); i++) {
            if (!WordAccountStatusContract.ifWordAccountStatusCursorContain(reallyLearnedWords.get(i),categoryName,accountName)) {
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

    private ArrayList<String> countToFive(ArrayList learnedWords){
        Map<String, Integer> occurrences = new HashMap<>();
        ArrayList<String> reallyLearnedWords = new ArrayList<>();
        for (int i=0; i<learnedWords.size(); i++) {
            occurrences.put(learnedWords.get(i).toString(), occurrences.containsKey(learnedWords.get(i).toString())
                    ? occurrences.get(learnedWords.get(i).toString()) + 1 : 1);
        }
        for (Map.Entry<String, Integer> entry : occurrences.entrySet()) {
            if(entry.getValue().equals(5)){
                reallyLearnedWords.add(entry.getKey());
            }
        }
        return reallyLearnedWords;
    }

    public void nextCategory(View view) {
        Intent myIntent;
        if(callingActivity.equals("com.example.blazej.languagelearner.LearningToGermanActivity")){
            myIntent = new Intent(this,ChoosingToPolishActivity.class);
            myIntent.putStringArrayListExtra("german_words",germanWordsInCategory);
            myIntent.putStringArrayListExtra("polish_words",polishWordsInCategory);
            myIntent.putStringArrayListExtra("learned_words",learnedWords);
            myIntent.putStringArrayListExtra("missed_words",missedWords);
            myIntent.putExtra("category_name",categoryName);
            myIntent.putExtra("account_name",accountName);
            startActivity(myIntent);
        }else if(callingActivity.equals("com.example.blazej.languagelearner.LearningToPolishActivity")){
            myIntent = new Intent(this,LearningToGermanActivity.class);
            myIntent.putStringArrayListExtra("german_words",germanWordsInCategory);
            myIntent.putStringArrayListExtra("polish_words",polishWordsInCategory);
            myIntent.putStringArrayListExtra("learned_words",learnedWords);
            myIntent.putStringArrayListExtra("missed_words",missedWords);
            myIntent.putExtra("category_name",categoryName);
            myIntent.putExtra("account_name",accountName);
            startActivity(myIntent);
        }else if(callingActivity.equals("com.example.blazej.languagelearner.ChoosingToPolishActivity")){
            myIntent = new Intent(this,ChoosingToGermanActivity.class);
            myIntent.putStringArrayListExtra("german_words",germanWordsInCategory);
            myIntent.putStringArrayListExtra("polish_words",polishWordsInCategory);
            myIntent.putStringArrayListExtra("learned_words",learnedWords);
            myIntent.putStringArrayListExtra("missed_words",missedWords);
            myIntent.putExtra("category_name",categoryName);
            myIntent.putExtra("account_name",accountName);
            startActivity(myIntent);
        }else if(callingActivity.equals("com.example.blazej.languagelearner.ChoosingToGermanActivity")){
            myIntent = new Intent(this,ListenActivity.class);
            myIntent.putStringArrayListExtra("german_words",germanWordsInCategory);
            myIntent.putStringArrayListExtra("polish_words",polishWordsInCategory);
            myIntent.putStringArrayListExtra("learned_words",learnedWords);
            myIntent.putStringArrayListExtra("missed_words",missedWords);
            myIntent.putExtra("category_name",categoryName);
            myIntent.putExtra("account_name",accountName);
            startActivity(myIntent);
        }else if(callingActivity.equals("com.example.blazej.languagelearner.ListenActivity")){
            myIntent = new Intent(this,MenuActivity.class);
            startActivity(myIntent);
        }
    }
}
