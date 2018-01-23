package com.example.blazej.languagelearner;

import android.content.Intent;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

public class ListenActivity extends AppCompatActivity implements View.OnClickListener {

    String accountName;
    TextView selectedCategoryTV;
    TextView whichQuestionTV;
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
    ArrayList<String> learnedWordsCategory = new ArrayList<>();
    ArrayList<String> missedWordsCategory = new ArrayList<>();
    ArrayList<String> categoriesOfWordsToReview = new ArrayList<>();
    String categoryName;
    Button nextQuestionBTN;
    Button ans1BTN;
    Button ans2BTN;
    Button ans3BTN;
    Button ans4BTN;
    Button listenBTN;
    TextToSpeech t1;
    int goodAnsIndex;
    ArrayList<Button> myButtonList = new ArrayList<>();
    AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
    AlphaAnimation fadeOut = new AlphaAnimation( 1.0f , 0.0f ) ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);
        selectedCategoryTV = (TextView)findViewById(R.id.selectedCategoryTV);
        whichQuestionTV = (TextView)findViewById(R.id.whichQuestionTV);
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
        listenBTN = (Button)findViewById(R.id.listenBTN);

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.GERMAN);
                }
            }
        });

        listenBTN.setOnClickListener(toListenListener());

        myButtonList.add(ans1BTN);
        myButtonList.add(ans2BTN);
        myButtonList.add(ans3BTN);
        myButtonList.add(ans4BTN);
        currentQuestion = 1;

        Intent intent = getIntent();
        germanWordsInCategory = intent.getStringArrayListExtra("german_words");
        polishWordsInCategory = intent.getStringArrayListExtra("polish_words");
        categoriesOfWordsToReview = intent.getStringArrayListExtra("word_category");
        learnedWords = intent.getStringArrayListExtra("learned_words");
        missedWords = intent.getStringArrayListExtra("missed_words");
        learnedWordsCategory = intent.getStringArrayListExtra("learned_words_category");
        missedWordsCategory = intent.getStringArrayListExtra("missed_words_category");
        accountName = intent.getStringExtra("account_name");
        categoryName = intent.getStringExtra("category_name");

        long seed = System.nanoTime();
        Collections.shuffle(germanWordsInCategory, new Random(seed));
        Collections.shuffle(polishWordsInCategory, new Random(seed));
        Collections.shuffle(categoriesOfWordsToReview, new Random(seed));
        questionCount = germanWordsInCategory.size();
        questionCore(questionCount,currentQuestion);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    private View.OnClickListener toListenListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak = germanWord;
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null,null);
                Log.v("TAG","germanWord: " + germanWord);
            }
        };}


    private void questionCore(int questionCount, int currentQuestion) {
        if(currentQuestion <= questionCount){
            whichQuestionTV.setText(currentQuestion + " z " + questionCount);
            int index = currentQuestion - 1;
            categoryName = categoriesOfWordsToReview.get(index);
            selectedCategoryTV.setText(getString(R.string.chosen_category,categoryName));
            germanWord = germanWordsInCategory.get(index);
            rightAnswer = polishWordsInCategory.get(index);
            int buttonIndexWithGoodAns = randInt(0,3);
            goodAnsIndex = buttonIndexWithGoodAns;
            myButtonList.get(buttonIndexWithGoodAns).setText(rightAnswer);
            int[] array1 = new int[2];
            int[] array2 = new int[3];
            switch(buttonIndexWithGoodAns){
                case 0:
                    Random rnd = new Random();
                    int badAns1 = getRandomWithExclusion(rnd,0,questionCount-1,index);
                    array1[0] = index;
                    array1[1] = badAns1;
                    Arrays.sort(array1);
                    int badAns2 = getRandomWithExclusion(rnd,0,questionCount-1,array1[0],array1[1]);
                    array2[0] = index;
                    array2[1] = badAns1;
                    array2[2] = badAns2;
                    Arrays.sort(array2);
                    int badAns3 = getRandomWithExclusion(rnd,0,questionCount-1,array2[0],array2[1],array2[2]);
                    myButtonList.get(1).setText(polishWordsInCategory.get(badAns1));
                    myButtonList.get(2).setText(polishWordsInCategory.get(badAns2));
                    myButtonList.get(3).setText(polishWordsInCategory.get(badAns3));
                    break;

                case 1:
                    rnd = new Random();
                    badAns1 = getRandomWithExclusion(rnd,0,questionCount-1,index);
                    array1[0] = index;
                    array1[1] = badAns1;
                    Arrays.sort(array1);
                    badAns2 = getRandomWithExclusion(rnd,0,questionCount-1,array1[0],array1[1]);
                    array2[0] = index;
                    array2[1] = badAns1;
                    array2[2] = badAns2;
                    Arrays.sort(array2);
                    badAns3 = getRandomWithExclusion(rnd,0,questionCount-1,array2[0],array2[1],array2[2]);
                    myButtonList.get(0).setText(polishWordsInCategory.get(badAns1));
                    myButtonList.get(2).setText(polishWordsInCategory.get(badAns2));
                    myButtonList.get(3).setText(polishWordsInCategory.get(badAns3));
                    break;
                case 2:
                    rnd = new Random();
                    badAns1 = getRandomWithExclusion(rnd,0,questionCount-1,index);
                    array1[0] = index;
                    array1[1] = badAns1;
                    Arrays.sort(array1);
                    badAns2 = getRandomWithExclusion(rnd,0,questionCount-1,array1[0],array1[1]);
                    array2[0] = index;
                    array2[1] = badAns1;
                    array2[2] = badAns2;
                    Arrays.sort(array2);
                    badAns3 = getRandomWithExclusion(rnd,0,questionCount-1,array2[0],array2[1],array2[2]);
                    myButtonList.get(0).setText(polishWordsInCategory.get(badAns1));
                    myButtonList.get(1).setText(polishWordsInCategory.get(badAns2));
                    myButtonList.get(3).setText(polishWordsInCategory.get(badAns3));
                    break;
                case 3:
                    rnd = new Random();
                    badAns1 = getRandomWithExclusion(rnd,0,questionCount-1,index);
                    array1[0] = index;
                    array1[1] = badAns1;
                    Arrays.sort(array1);
                    badAns2 = getRandomWithExclusion(rnd,0,questionCount-1,array1[0],array1[1]);
                    array2[0] = index;
                    array2[1] = badAns1;
                    array2[2] = badAns2;
                    Arrays.sort(array2);
                    badAns3 = getRandomWithExclusion(rnd,0,questionCount-1,array2[0],array2[1],array2[2]);
                    myButtonList.get(0).setText(polishWordsInCategory.get(badAns1));
                    myButtonList.get(1).setText(polishWordsInCategory.get(badAns2));
                    myButtonList.get(2).setText(polishWordsInCategory.get(badAns3));
                    break;
            }
        }else{
            showResult();
        }
    }

    private int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public int getRandomWithExclusion(Random rnd, int start, int end, int... exclude) {
        int random = start + rnd.nextInt(end - start + 1 - exclude.length);
        for (int ex : exclude) {
            if (random < ex) {
                break;
            }
            random++;
        }
        return random;
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
        myIntent.putExtra("account_name",accountName);
        startActivityForResult(myIntent,1);
    }

    @Override
    public void onClick(View v) {
        int index = currentQuestion - 1;
        switch(v.getId()){
            case R.id.ans1BTN:
                if(goodAnsIndex == 0){
                    learnedWords.add(rightAnswer);
                    learnedWordsCategory.add(categoriesOfWordsToReview.get(index));
                    yourAnswerTV.setText(getString(R.string.your_answer,ans1BTN.getText()));
                    yourAnswerTV.setTextColor(Color.GREEN);
                }else{
                    missedWords.add(rightAnswer);
                    missedWordsCategory.add(categoriesOfWordsToReview.get(index));
                    yourAnswerTV.setText(getString(R.string.your_answer,ans1BTN.getText()));
                    yourAnswerTV.setTextColor(Color.WHITE);
                }
                rightAnswerTV.setText(getString(R.string.right_answer,rightAnswer));
                break;
            case R.id.ans2BTN:
                if(goodAnsIndex == 1){
                    learnedWords.add(rightAnswer);
                    learnedWordsCategory.add(categoriesOfWordsToReview.get(index));
                    yourAnswerTV.setText(getString(R.string.your_answer,ans2BTN.getText()));
                    yourAnswerTV.setTextColor(Color.GREEN);
                }else{
                    missedWords.add(rightAnswer);
                    missedWordsCategory.add(categoriesOfWordsToReview.get(index));
                    yourAnswerTV.setText(getString(R.string.your_answer,ans2BTN.getText()));
                    yourAnswerTV.setTextColor(Color.WHITE);
                }
                rightAnswerTV.setText(getString(R.string.right_answer,rightAnswer));
                break;
            case R.id.ans3BTN:
                if(goodAnsIndex == 2){
                    learnedWords.add(rightAnswer);
                    learnedWordsCategory.add(categoriesOfWordsToReview.get(index));
                    yourAnswerTV.setText(getString(R.string.your_answer,ans3BTN.getText()));
                    yourAnswerTV.setTextColor(Color.GREEN);
                }else{
                    missedWords.add(rightAnswer);
                    missedWordsCategory.add(categoriesOfWordsToReview.get(index));
                    yourAnswerTV.setText(getString(R.string.your_answer,ans3BTN.getText()));
                    yourAnswerTV.setTextColor(Color.WHITE);
                }
                rightAnswerTV.setText(getString(R.string.right_answer,rightAnswer));
                break;
            case R.id.ans4BTN:
                if(goodAnsIndex == 3){
                    learnedWords.add(rightAnswer);
                    learnedWordsCategory.add(categoriesOfWordsToReview.get(index));
                    yourAnswerTV.setText(getString(R.string.your_answer,ans4BTN.getText()));
                    yourAnswerTV.setTextColor(Color.GREEN);
                }else{
                    missedWords.add(rightAnswer);
                    missedWordsCategory.add(categoriesOfWordsToReview.get(index));
                    yourAnswerTV.setText(getString(R.string.your_answer,ans4BTN.getText()));
                    yourAnswerTV.setTextColor(Color.WHITE);
                }
                rightAnswerTV.setText(getString(R.string.right_answer,rightAnswer));
                break;
            default:
                break;
        }
        rightAnswerTV.startAnimation(fadeIn);
        yourAnswerTV.startAnimation(fadeIn);
        fadeIn.setDuration(800);
        fadeIn.setFillAfter(true);
        ans1BTN.setVisibility(View.INVISIBLE);
        ans2BTN.setVisibility(View.INVISIBLE);
        ans3BTN.setVisibility(View.INVISIBLE);
        ans4BTN.setVisibility(View.INVISIBLE);
        listenBTN.setVisibility(View.INVISIBLE);
        nextQuestionBTN.setVisibility(View.VISIBLE);
    }

    public void nextQuestionBTN(View view) {
        currentQuestion ++;
        ans1BTN.setVisibility(View.VISIBLE);
        ans2BTN.setVisibility(View.VISIBLE);
        ans3BTN.setVisibility(View.VISIBLE);
        ans4BTN.setVisibility(View.VISIBLE);
        listenBTN.setVisibility(View.VISIBLE);
        nextQuestionBTN.setVisibility(View.INVISIBLE);
        rightAnswerTV.startAnimation(fadeOut);
        yourAnswerTV.startAnimation(fadeOut);
        fadeOut.setDuration(100);
        fadeOut.setFillAfter(true);
        questionCore(questionCount,currentQuestion);
    }

}
