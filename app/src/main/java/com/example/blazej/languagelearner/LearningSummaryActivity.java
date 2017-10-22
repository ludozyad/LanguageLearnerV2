package com.example.blazej.languagelearner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class LearningSummaryActivity extends AppCompatActivity {
    ArrayList<String> germanWordsInCategory = new ArrayList<>();
    ArrayList<String> polishWordsInCategory = new ArrayList<>();
    String callingActivity;
    String categoryName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_summary);
        callingActivity = getCallingActivity().getClassName();
        Log.v("TAG", "Calling activity: " + callingActivity);
        Intent intent = getIntent();

        switch (callingActivity){
            case "com.example.blazej.languagelearner.LearningToGermanActivity":
                germanWordsInCategory = intent.getStringArrayListExtra("german_words");
                polishWordsInCategory = intent.getStringArrayListExtra("polish_words");
                categoryName = intent.getStringExtra("selected_category");
                break;
            case "com.example.blazej.languagelearner.LearningToPolishActivity":
                germanWordsInCategory = intent.getStringArrayListExtra("german_words");
                polishWordsInCategory = intent.getStringArrayListExtra("polish_words");
                categoryName = intent.getStringExtra("selected_category");
                break;
            case "com.example.blazej.languagelearner.ChoosingToPolishActivity":
                germanWordsInCategory = intent.getStringArrayListExtra("german_words");
                polishWordsInCategory = intent.getStringArrayListExtra("polish_words");
                categoryName = intent.getStringExtra("selected_category");
                break;
            case "com.example.blazej.languagelearner.ChoosingToGermanActivity":
                germanWordsInCategory = intent.getStringArrayListExtra("german_words");
                polishWordsInCategory = intent.getStringArrayListExtra("polish_words");
                categoryName = intent.getStringExtra("selected_category");
                break;
        }

    }

    public void nextCategory(View view) {
        Intent myIntent;
        if(callingActivity.equals("com.example.blazej.languagelearner.LearningToGermanActivity")){
            myIntent = new Intent(this,ChoosingToPolishActivity.class);
            myIntent.putStringArrayListExtra("german_words",germanWordsInCategory);
            myIntent.putStringArrayListExtra("polish_words",polishWordsInCategory);
            myIntent.putExtra("selected_category",categoryName);
            startActivity(myIntent);
        }else if(callingActivity.equals("com.example.blazej.languagelearner.LearningToPolishActivity")){
            myIntent = new Intent(this,LearningToGermanActivity.class);
            myIntent.putStringArrayListExtra("german_words",germanWordsInCategory);
            myIntent.putStringArrayListExtra("polish_words",polishWordsInCategory);
            myIntent.putExtra("selected_category",categoryName);
            startActivity(myIntent);
        }else if(callingActivity.equals("com.example.blazej.languagelearner.ChoosingToPolishActivity")){
            myIntent = new Intent(this,ChoosingToGermanActivity.class);
            myIntent.putStringArrayListExtra("german_words",germanWordsInCategory);
            myIntent.putStringArrayListExtra("polish_words",polishWordsInCategory);
            myIntent.putExtra("selected_category",categoryName);
            startActivity(myIntent);
        }else if(callingActivity.equals("com.example.blazej.languagelearner.ChoosingToGermanActivity")){
            myIntent = new Intent(this,ListenActivity.class);
            myIntent.putStringArrayListExtra("german_words",germanWordsInCategory);
            myIntent.putStringArrayListExtra("polish_words",polishWordsInCategory);
            myIntent.putExtra("selected_category",categoryName);
            startActivity(myIntent);
        }else if(callingActivity.equals("com.example.blazej.languagelearner.ListenActivity")){
            myIntent = new Intent(this,MenuActivity.class);
            startActivity(myIntent);
        }
    }
}
