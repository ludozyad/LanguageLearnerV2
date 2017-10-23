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
import java.util.Random;

public class LearningToPolishActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    String accountName;
    public String selectedCategory = "category";
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
    ArrayList<String> learnedWords = new ArrayList<>();
    ArrayList<String> missedWords = new ArrayList<>();
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
        WordsDbHelper myDBHelper = new WordsDbHelper(this);
        myDB = myDBHelper.getWritableDatabase();

        wordAccountStatusCursor = WordAccountStatusContract.getWordAccountStatusCursor();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                categoryName = null;
            } else {
                categoryName = extras.getString(selectedCategory);
            }
        } else {
            categoryName = (String) savedInstanceState.getSerializable(selectedCategory);
        }
        selectedCategoryTV.setText("Selected Category: " + categoryName);

        Cursor wordsInCategory = getWordsInCategory(categoryName);

        while(wordsInCategory.moveToNext()){
            germanWordsInCategory.add(wordsInCategory.getString(0));
            polishWordsInCategory.add(wordsInCategory.getString(1));
        }
        long seed = System.nanoTime();
        Collections.shuffle(germanWordsInCategory, new Random(seed));
        Collections.shuffle(polishWordsInCategory, new Random(seed));

        currentQuestion = 1;
        questionCount = wordsInCategory.getCount();
        questionCore(questionCount, currentQuestion);

    }

    private void questionCore(int questionCount, int currentQuestion) {
        if(currentQuestion <= questionCount){
            whichQuestionTV.setText(currentQuestion + " of " + questionCount);
            int index = currentQuestion - 1;
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
                Toast.makeText(this, "Poprawna Odpowiedź!", Toast.LENGTH_SHORT).show();
                yourAnswerTV.setText("Your Answer: " + yourAnswer);
                rightAnswerTV.setText("Right Answer: " + rightAnswer);
                learnedWords.add(rightAnswer);
                yourAnswerTV.setTextColor(Color.GREEN);
            }else{
                Toast.makeText(this, "Błędna Odpowiedź!", Toast.LENGTH_SHORT).show();
                yourAnswerTV.setText("Your Answer: " + yourAnswer);
                rightAnswerTV.setText("Right Answer: " + rightAnswer);
                missedWords.add(rightAnswer);
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
        myIntent.putExtra("category_name",categoryName);
        myIntent.putExtra("account_name",accountName);
        Log.v("TAG", "Account Name: " + accountName+ " --- Selected Category: " + categoryName);
        startActivityForResult(myIntent,1);
    }

    Cursor getWordsInCategory(String category){
        return  myDB.query(WordListContract.DatabaseColumnsEntry.TABLE_NAME,
                new String[] {WordListContract.DatabaseColumnsEntry.GERMAN_COLUMN_NAME,WordListContract.DatabaseColumnsEntry.POLISH_COLUMN_NAME},
                WordListContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME + " = ?",
                new String[]{category},
                null,
                null,
                null);

    }

}
