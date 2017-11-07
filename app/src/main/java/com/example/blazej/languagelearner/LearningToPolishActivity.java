package com.example.blazej.languagelearner;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blazej.languagelearner.data.AccountListContract;
import com.example.blazej.languagelearner.data.WordAccountStatusContract;
import com.example.blazej.languagelearner.data.WordAccountStatusDbHelper;
import com.example.blazej.languagelearner.data.WordListContract;
import com.example.blazej.languagelearner.data.WordsDbHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LearningToPolishActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    String accountName;
    String callingActivity;
    //public String selectedCategory = "category";
    TextView selectedCategoryTV;
    TextView whichQuestionTV;
    TextView toLearnWordTV;
    TextView rightAnswerTV;
    TextView yourAnswerTV;
    EditText enterWordET;
    Button checkAnswerBTN;
    Button nextQuestionBTN;
    SQLiteDatabase myDB;
    String germanWord;
    String rightAnswer;
    int questionCount;
    int currentQuestion;
    ArrayList<String> germanWordsInCategory = new ArrayList<>();
    ArrayList<String> polishWordsInCategory = new ArrayList<>();
    ArrayList<String> learnedWordsCategory = new ArrayList<>();
    ArrayList<String> missedWordsCategory = new ArrayList<>();
    ArrayList<String> learnedWords = new ArrayList<>();
    ArrayList<String> missedWords = new ArrayList<>();
    ArrayList<String> categoriesOfWordsToReview = new ArrayList<>();
    String categoryName;
    Cursor wordAccountStatusCursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);
        sharedPref = getSharedPreferences(AccountListContract.sharedName,MODE_PRIVATE);
        accountName = sharedPref.getString(AccountListContract.sharedName,"XD");
        Log.v("TAG","Imie w Learing to polish activity: " + accountName);
        selectedCategoryTV = (TextView)findViewById(R.id.selectedCategoryTV);
        whichQuestionTV = (TextView)findViewById(R.id.whichQuestionTV);
        toLearnWordTV = (TextView)findViewById(R.id.toLearnWordTV);
        rightAnswerTV = (TextView)findViewById(R.id.rightAnswerTV);
        rightAnswerTV.setVisibility(View.INVISIBLE);
        yourAnswerTV = (TextView)findViewById(R.id.yourAnswerTV);
        yourAnswerTV.setVisibility(View.INVISIBLE);
        enterWordET = (EditText)findViewById(R.id.enterWordET);
        checkAnswerBTN = (Button)findViewById(R.id.checkAnswerBTN);
        nextQuestionBTN = (Button)findViewById(R.id.nextQuestionBTN);
        nextQuestionBTN.setVisibility(View.INVISIBLE);




        /////////////
        Intent intent = getIntent();
        callingActivity = getCallingActivity().getClassName();
        switch (callingActivity){
            case "com.example.blazej.languagelearner.ChooseCategoryActivity":
                WordsDbHelper myDBHelper = new WordsDbHelper(this);
                myDB = myDBHelper.getWritableDatabase();
                wordAccountStatusCursor = WordAccountStatusContract.getWordAccountStatusCursor();
                //categoryName = intent.getStringExtra("category_name");
                selectedCategoryTV.setText(getString(R.string.chosen_category,categoryName));
                questionCount = intent.getIntExtra("category_count",0);
                germanWordsInCategory = intent.getStringArrayListExtra("german_words");
                polishWordsInCategory = intent.getStringArrayListExtra("polish_words");
                categoriesOfWordsToReview = intent.getStringArrayListExtra("word_category");
                /*
                Cursor wordsInCategory = getWordsInCategory(categoryName);
                while(wordsInCategory.moveToNext()){
                    germanWordsInCategory.add(wordsInCategory.getString(0));
                    polishWordsInCategory.add(wordsInCategory.getString(1));
                }
                */
                Log.v("TAG","questionCount: " + questionCount);
                long seed = System.nanoTime();
                Collections.shuffle(germanWordsInCategory, new Random(seed));
                Collections.shuffle(polishWordsInCategory, new Random(seed));
                Collections.shuffle(categoriesOfWordsToReview, new Random(seed));
                categoriesOfWordsToReview = new ArrayList<>(categoriesOfWordsToReview.subList(0,questionCount));
                germanWordsInCategory = new ArrayList<>(germanWordsInCategory.subList(0,questionCount));
                polishWordsInCategory = new ArrayList<>(polishWordsInCategory.subList(0,questionCount));
                ///////////////////////////
                break;
            case "com.example.blazej.languagelearner.ReviewActivity":
                questionCount = intent.getIntExtra("words_to_review_count",0);
                Log.v("TAG","questionCount: " + questionCount);
                germanWordsInCategory = intent.getStringArrayListExtra("german_words");
                Log.v("TAG","germanWordsInCategory: " + germanWordsInCategory.size());
                polishWordsInCategory = intent.getStringArrayListExtra("polish_words");
                Log.v("TAG","polishWordsInCategory: " + polishWordsInCategory.size());
                categoriesOfWordsToReview = intent.getStringArrayListExtra("word_category");
                Log.v("TAG","categoriesOfWordsToReview: " + categoriesOfWordsToReview.size());
                long seed2 = System.nanoTime();
                Collections.shuffle(germanWordsInCategory, new Random(seed2));
                Collections.shuffle(polishWordsInCategory, new Random(seed2));
                Collections.shuffle(categoriesOfWordsToReview, new Random(seed2));
                categoriesOfWordsToReview = new ArrayList<>(categoriesOfWordsToReview.subList(0,questionCount));
                germanWordsInCategory = new ArrayList<>(germanWordsInCategory.subList(0,questionCount));
                polishWordsInCategory = new ArrayList<>(polishWordsInCategory.subList(0,questionCount));

                Log.v("TAG","TU WCHODZIMY DO LEARNINGTOPOLISH!@!@#@#");
                for(int i=0;i<categoriesOfWordsToReview.size();i++){
                    Log.v("TAG","categoriesOfWordsToReview: " + categoriesOfWordsToReview.get(i));
                    Log.v("TAG","germanWordsInCategory: " + germanWordsInCategory.get(i));
                    Log.v("TAG","polishWordsInCategory: " + polishWordsInCategory.get(i));
                }
                //categoryName = "";
                break;
        }
        currentQuestion = 1;
        questionCore(questionCount, currentQuestion);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    private void questionCore(int questionCount, int currentQuestion) {
        if(currentQuestion <= questionCount){
            whichQuestionTV.setText(currentQuestion + " z " + questionCount);
            int index = currentQuestion - 1;
            categoryName = categoriesOfWordsToReview.get(index);
            selectedCategoryTV.setText(getString(R.string.chosen_category,categoryName));
            germanWord = germanWordsInCategory.get(index);
            toLearnWordTV.setText(germanWord);
        }else{
            Toast.makeText(this, "Koniec Pytań!", Toast.LENGTH_SHORT).show();
            showResult();
        }
    }

    public void nextQuestionBTN(View view) {
        rightAnswerTV.setVisibility(View.INVISIBLE);
        yourAnswerTV.setVisibility(View.INVISIBLE);
        checkAnswerBTN.setVisibility(View.VISIBLE);
        enterWordET.setVisibility(View.VISIBLE);
        nextQuestionBTN.setVisibility(View.INVISIBLE);
        currentQuestion++;
        questionCore(questionCount, currentQuestion);
    }


    public void checkAnswerBTN(View view) {
        if(enterWordET.getText().length() > 0) {
            String yourAnswer = enterWordET.getText().toString();
            int index = currentQuestion - 1;
            rightAnswer = polishWordsInCategory.get(index);
            if (yourAnswer.equals(rightAnswer)){
                Toast.makeText(this,R.string.congrats, Toast.LENGTH_SHORT).show();
                yourAnswerTV.setText(getString(R.string.your_answer,yourAnswer));
                rightAnswerTV.setText(getString(R.string.right_answer,rightAnswer));
                learnedWords.add(rightAnswer);
                learnedWordsCategory.add(categoriesOfWordsToReview.get(index));
                yourAnswerTV.setTextColor(Color.GREEN);
            }else{
                Toast.makeText(this, R.string.better_luck_next_time, Toast.LENGTH_SHORT).show();
                yourAnswerTV.setText(getString(R.string.your_answer,yourAnswer));

                rightAnswerTV.setText(getString(R.string.right_answer,rightAnswer));
                missedWords.add(rightAnswer);
                missedWordsCategory.add(categoriesOfWordsToReview.get(index));
                yourAnswerTV.setTextColor(Color.RED);
            }
            rightAnswerTV.setVisibility(View.VISIBLE);
            yourAnswerTV.setVisibility(View.VISIBLE);
            checkAnswerBTN.setVisibility(View.INVISIBLE);
            enterWordET.getText().clear();
            enterWordET.setVisibility(View.INVISIBLE);
            nextQuestionBTN.setVisibility(View.VISIBLE);
            questionCore(questionCount, currentQuestion);
        }else{
            Toast.makeText(this, "Pole nie może być puste!", Toast.LENGTH_SHORT).show();
        }
    }


    private void showResult() {
        Intent myIntent = new Intent(this,LearningSummaryActivity.class);
        myIntent.putStringArrayListExtra("german_words",germanWordsInCategory);
        myIntent.putStringArrayListExtra("polish_words",polishWordsInCategory);
        myIntent.putStringArrayListExtra("learned_words",learnedWords);
        myIntent.putStringArrayListExtra("missed_words",missedWords);
        myIntent.putStringArrayListExtra("learned_words_category",learnedWordsCategory);
        myIntent.putStringArrayListExtra("missed_words_category",missedWordsCategory);

        myIntent.putStringArrayListExtra("word_category",categoriesOfWordsToReview);

        //myIntent.putExtra("category_name",categoryName);
        myIntent.putExtra("account_name",accountName);
        Log.v("TAG", "Account Name: " + accountName+ " --- Selected Category: " + categoryName);
        startActivityForResult(myIntent,1);
    }
}
