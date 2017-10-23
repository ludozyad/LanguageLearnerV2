package com.example.blazej.languagelearner;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class ChoosingToPolishActivity extends AppCompatActivity implements View.OnClickListener {


    String accountName;
    TextView selectedCategoryTV;
    TextView whichQuestionTV;
    TextView toLearnWordTV;
    TextView rightAnswerTV;
    TextView yourAnswerTV;
    int questionCount;
    int currentQuestion;
    String germanWord;
    String rightAnswer;
    ArrayList<String> germanWordsInCategory = new ArrayList<>();
    ArrayList<String> polishWordsInCategory = new ArrayList<>();
    ArrayList<String> learnedWords = new ArrayList<>();
    ArrayList<String> missedWords = new ArrayList<>();
    String categoryName;
    Button nextQuestionBTN;
    Button ans1BTN;
    Button ans2BTN;
    Button ans3BTN;
    Button ans4BTN;
    int goodAnsIndex;
    ArrayList<Button> myButtonList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosing);
        selectedCategoryTV = (TextView)findViewById(R.id.selectedCategoryTV);
        whichQuestionTV = (TextView)findViewById(R.id.whichQuestionTV);
        toLearnWordTV = (TextView)findViewById(R.id.toLearnWordTV);
        rightAnswerTV = (TextView)findViewById(R.id.rightAnswerTV);
        rightAnswerTV.setVisibility(View.INVISIBLE);
        yourAnswerTV = (TextView)findViewById(R.id.yourAnswerTV);
        yourAnswerTV.setVisibility(View.INVISIBLE);
        nextQuestionBTN = (Button)findViewById(R.id.nextQuestionBTN);
        nextQuestionBTN.setVisibility(View.INVISIBLE);
        ans1BTN = (Button)findViewById(R.id.ans1BTN);
        ans1BTN.setOnClickListener(this);
        ans2BTN = (Button)findViewById(R.id.ans2BTN);
        ans2BTN.setOnClickListener(this);
        ans3BTN = (Button)findViewById(R.id.ans3BTN);
        ans3BTN.setOnClickListener(this);
        ans4BTN = (Button)findViewById(R.id.ans4BTN);
        ans4BTN.setOnClickListener(this);
        myButtonList.add(ans1BTN);
        myButtonList.add(ans2BTN);
        myButtonList.add(ans3BTN);
        myButtonList.add(ans4BTN);
        currentQuestion = 1;

        //getting data from intent
        Intent intent = getIntent();
        germanWordsInCategory = intent.getStringArrayListExtra("german_words");
        polishWordsInCategory = intent.getStringArrayListExtra("polish_words");
        learnedWords = intent.getStringArrayListExtra("learned_words");
        missedWords = intent.getStringArrayListExtra("missed_words");
        accountName = intent.getStringExtra("account_name");
        categoryName = intent.getStringExtra("category_name");
        //////////////////////////////

        long seed = System.nanoTime();
        Collections.shuffle(germanWordsInCategory, new Random(seed));
        Collections.shuffle(polishWordsInCategory, new Random(seed));
        selectedCategoryTV.setText(categoryName);
        questionCount = germanWordsInCategory.size();

        questionCore(questionCount,currentQuestion);
    }

    private void questionCore(int questionCount, int currentQuestion) {
        if(currentQuestion <= questionCount){
            whichQuestionTV.setText(currentQuestion + " of " + questionCount);
            int index = currentQuestion - 1;
            // To co mamy przetlumaczyć
            germanWord = germanWordsInCategory.get(index);
            // Dobra odpowiedz
            rightAnswer = polishWordsInCategory.get(index);
            toLearnWordTV.setText(germanWord);
            int buttonIndexWithGoodAns = randInt(0,3);
            goodAnsIndex = buttonIndexWithGoodAns;
            myButtonList.get(buttonIndexWithGoodAns).setText(rightAnswer);
            switch(buttonIndexWithGoodAns){
                case 0:
                    Log.v("TAG", "Case 0");
                    myButtonList.get(1).setText(polishWordsInCategory.get(randIntWithout(0,questionCount-1,index)));
                    myButtonList.get(2).setText(polishWordsInCategory.get(randIntWithout(0,questionCount-1,index)));
                    myButtonList.get(3).setText(polishWordsInCategory.get(randIntWithout(0,questionCount-1,index)));
                    break;
                case 1:
                    Log.v("TAG", "Case 1");
                    myButtonList.get(0).setText(polishWordsInCategory.get(randIntWithout(0,questionCount-1,index)));
                    myButtonList.get(2).setText(polishWordsInCategory.get(randIntWithout(0,questionCount-1,index)));
                    myButtonList.get(3).setText(polishWordsInCategory.get(randIntWithout(0,questionCount-1,index)));
                    break;
                case 2:
                    Log.v("TAG", "Case 2");
                    myButtonList.get(0).setText(polishWordsInCategory.get(randIntWithout(0,questionCount-1,index)));
                    myButtonList.get(1).setText(polishWordsInCategory.get(randIntWithout(0,questionCount-1,index)));
                    myButtonList.get(3).setText(polishWordsInCategory.get(randIntWithout(0,questionCount-1,index)));
                    break;
                case 3:
                    Log.v("TAG", "Case 3");
                    myButtonList.get(0).setText(polishWordsInCategory.get(randIntWithout(0,questionCount-1,index)));
                    myButtonList.get(1).setText(polishWordsInCategory.get(randIntWithout(0,questionCount-1,index)));
                    myButtonList.get(2).setText(polishWordsInCategory.get(randIntWithout(0,questionCount-1,index)));
                    break;
            }
            //dotad działa
        }else{
            Toast.makeText(this, "Koniec Pytań!", Toast.LENGTH_SHORT).show();
            showResult();
        }
    }

    private int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        Log.v("TAG", "randomNum (bez witout): " + randomNum);
        return randomNum;
    }

    public static int randIntWithout(int min, int max, int without) {
        Random rand = new Random();
        int randomNum;
        do {
            randomNum = rand.nextInt((max - min) + 1) + min;
        }while(randomNum == without);
        Log.v("TAG", "randIntWithout: " + randomNum);
        return randomNum;
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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ans1BTN:
                if(goodAnsIndex == 0){
                    learnedWords.add(rightAnswer);
                    yourAnswerTV.setText("Your answer: " + ans1BTN.getText());
                    yourAnswerTV.setTextColor(Color.GREEN);
                }else{
                    missedWords.add(rightAnswer);
                    yourAnswerTV.setText("Your answer: " + ans1BTN.getText());
                    yourAnswerTV.setTextColor(Color.RED);
                }
                rightAnswerTV.setText("Right answer: " + rightAnswer);
                rightAnswerTV.setVisibility(View.VISIBLE);
                yourAnswerTV.setVisibility(View.VISIBLE);
                ans1BTN.setVisibility(View.INVISIBLE);
                ans2BTN.setVisibility(View.INVISIBLE);
                ans3BTN.setVisibility(View.INVISIBLE);
                ans4BTN.setVisibility(View.INVISIBLE);
                nextQuestionBTN.setVisibility(View.VISIBLE);
                break;
            case R.id.ans2BTN:
                if(goodAnsIndex == 1){
                    learnedWords.add(rightAnswer);
                    yourAnswerTV.setText("Your answer: " + ans2BTN.getText());
                    yourAnswerTV.setTextColor(Color.GREEN);
                }else{
                    missedWords.add(rightAnswer);
                    yourAnswerTV.setText("Your answer: " + ans2BTN.getText());
                    yourAnswerTV.setTextColor(Color.RED);
                }
                rightAnswerTV.setText("Right answer: " + rightAnswer);
                rightAnswerTV.setVisibility(View.VISIBLE);
                yourAnswerTV.setVisibility(View.VISIBLE);
                ans1BTN.setVisibility(View.INVISIBLE);
                ans2BTN.setVisibility(View.INVISIBLE);
                ans3BTN.setVisibility(View.INVISIBLE);
                ans4BTN.setVisibility(View.INVISIBLE);
                nextQuestionBTN.setVisibility(View.VISIBLE);
                break;
            case R.id.ans3BTN:
                if(goodAnsIndex == 2){
                    learnedWords.add(rightAnswer);
                    yourAnswerTV.setText("Your answer: " + ans3BTN.getText());
                    yourAnswerTV.setTextColor(Color.GREEN);
                }else{
                    missedWords.add(rightAnswer);
                    yourAnswerTV.setText("Your answer: " + ans3BTN.getText());
                    yourAnswerTV.setTextColor(Color.RED);
                }
                rightAnswerTV.setText("Right answer: " + rightAnswer);
                rightAnswerTV.setVisibility(View.VISIBLE);
                yourAnswerTV.setVisibility(View.VISIBLE);
                ans1BTN.setVisibility(View.INVISIBLE);
                ans2BTN.setVisibility(View.INVISIBLE);
                ans3BTN.setVisibility(View.INVISIBLE);
                ans4BTN.setVisibility(View.INVISIBLE);
                nextQuestionBTN.setVisibility(View.VISIBLE);
                break;
            case R.id.ans4BTN:
                if(goodAnsIndex == 3){
                    learnedWords.add(rightAnswer);
                    yourAnswerTV.setText("Your answer: " + ans4BTN.getText());
                    yourAnswerTV.setTextColor(Color.GREEN);
                }else{
                    missedWords.add(rightAnswer);
                    yourAnswerTV.setText("Your answer: " + ans4BTN.getText());
                    yourAnswerTV.setTextColor(Color.RED);
                }
                rightAnswerTV.setText("Right answer: " + rightAnswer);
                rightAnswerTV.setVisibility(View.VISIBLE);
                yourAnswerTV.setVisibility(View.VISIBLE);
                ans1BTN.setVisibility(View.INVISIBLE);
                ans2BTN.setVisibility(View.INVISIBLE);
                ans3BTN.setVisibility(View.INVISIBLE);
                ans4BTN.setVisibility(View.INVISIBLE);
                nextQuestionBTN.setVisibility(View.VISIBLE);
                break;
            default:
                Log.v("TAG", "Zaden z przycisków");
                break;
        }
    }

    public void nextQuestionBTN(View view) {
        currentQuestion ++;
        ans1BTN.setVisibility(View.VISIBLE);
        ans2BTN.setVisibility(View.VISIBLE);
        ans3BTN.setVisibility(View.VISIBLE);
        ans4BTN.setVisibility(View.VISIBLE);
        nextQuestionBTN.setVisibility(View.INVISIBLE);
        rightAnswerTV.setVisibility(View.INVISIBLE);
        yourAnswerTV.setVisibility(View.INVISIBLE);
        questionCore(questionCount,currentQuestion);
    }
}
