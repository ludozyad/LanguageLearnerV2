package com.example.blazej.languagelearner;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blazej.languagelearner.data.WordAccountStatusContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class LearningToGermanActivity extends AppCompatActivity {

    TextView selectedCategoryTV;
    TextView whichQuestionTV;
    TextView toLearnWordTV;
    TextView rightAnswerTV;
    TextView yourAnswerTV;
    EditText enterWordET;
    Button checkAnswerBTN;
    Button nextQuestionBTN;
    String polishWord;
    String rightAnswer;
    int questionCount;
    int currentQuestion;
    ArrayList<String> germanWordsInCategory = new ArrayList<>();
    ArrayList<String> polishWordsInCategory = new ArrayList<>();
    ArrayList<String> learnedWords = new ArrayList<>();
    ArrayList<String> missedWords = new ArrayList<>();
    ArrayList<String> learnedWordsCategory = new ArrayList<>();
    ArrayList<String> missedWordsCategory = new ArrayList<>();
    ArrayList<String> categoriesOfWordsToReview = new ArrayList<>();
    String categoryName;
    String accountName;
    Cursor wordAccountStatusCursor;
    AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
    AlphaAnimation fadeOut = new AlphaAnimation( 1.0f , 0.0f ) ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);
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
        currentQuestion = 1;

        //getting data from intent
        Intent intent = getIntent();
        germanWordsInCategory = intent.getStringArrayListExtra("german_words");
        polishWordsInCategory = intent.getStringArrayListExtra("polish_words");
        learnedWords = intent.getStringArrayListExtra("learned_words");
        missedWords = intent.getStringArrayListExtra("missed_words");
        learnedWordsCategory = intent.getStringArrayListExtra("learned_words_category");
        missedWordsCategory = intent.getStringArrayListExtra("missed_words_category");
        accountName = intent.getStringExtra("account_name");
        //categoryName = intent.getStringExtra("category_name");
        categoriesOfWordsToReview = intent.getStringArrayListExtra("word_category");
        //////////////////////////////

        long seed = System.nanoTime();
        Collections.shuffle(germanWordsInCategory, new Random(seed));
        Collections.shuffle(polishWordsInCategory, new Random(seed));
        Collections.shuffle(categoriesOfWordsToReview, new Random(seed));
        //categoryName = intent.getStringExtra("category_name");
        //selectedCategoryTV.setText(categoryName);
        questionCount = germanWordsInCategory.size();
        wordAccountStatusCursor = WordAccountStatusContract.getWordAccountStatusCursor();
        questionCore(questionCount, currentQuestion);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    private void questionCore(int questionCount, int currentQuestion) {
        if(currentQuestion <= questionCount){
            whichQuestionTV.setText(currentQuestion + " of " + questionCount);
            int index = currentQuestion - 1;
            categoryName = categoriesOfWordsToReview.get(index);
            selectedCategoryTV.setText(getString(R.string.chosen_category,categoryName));
            polishWord = polishWordsInCategory.get(index);
            toLearnWordTV.setText(polishWord);
        }else{
            Toast.makeText(this, R.string.end_of_questions, Toast.LENGTH_SHORT).show();
            showResult();
        }
    }

    public void nextQuestionBTN(View view) {
        rightAnswerTV.startAnimation(fadeOut);
        yourAnswerTV.startAnimation(fadeOut);
        fadeOut.setDuration(100);
        fadeOut.setFillAfter(true);
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
            rightAnswer = germanWordsInCategory.get(index);
            if (yourAnswer.equals(rightAnswer)){
                Toast.makeText(this, R.string.congrats, Toast.LENGTH_SHORT).show();
                yourAnswerTV.setText(getString(R.string.your_answer,yourAnswer));
                rightAnswerTV.setText(getString(R.string.right_answer,rightAnswer));
                learnedWords.add(polishWord);
                learnedWordsCategory.add(categoriesOfWordsToReview.get(index));
                yourAnswerTV.setTextColor(Color.GREEN);
            }else{
                Toast.makeText(this, R.string.better_luck_next_time, Toast.LENGTH_SHORT).show();
                yourAnswerTV.setText(getString(R.string.your_answer,yourAnswer));
                rightAnswerTV.setText(getString(R.string.right_answer,rightAnswer));
                missedWords.add(polishWord);
                missedWordsCategory.add(categoriesOfWordsToReview.get(index));
                yourAnswerTV.setTextColor(Color.WHITE);
            }
            rightAnswerTV.startAnimation(fadeIn);
            yourAnswerTV.startAnimation(fadeIn);
            fadeIn.setDuration(800);
            fadeIn.setFillAfter(true);
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
