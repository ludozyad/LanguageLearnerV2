package com.example.blazej.languagelearner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.blazej.languagelearner.data.AccountListContract;
import com.example.blazej.languagelearner.data.WordAccountStatusContract;
import com.example.blazej.languagelearner.data.WordListContract;
import com.example.blazej.languagelearner.data.WordsDbHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class ReviewActivity extends AppCompatActivity {

    SharedPreferences sharedPref;
    String accountName;
    NumberPicker numberPicker;
    Cursor reviewCursor;
    ArrayList<String> wordsToReview = new ArrayList<>();
    ArrayList<String> germanWordsByAccount = new ArrayList<>();
    ArrayList<String> polishWordsByAccount = new ArrayList<>();
    ArrayList<String> categoriesOfWordsToReview = new ArrayList<>();
    Button startReviewBTN;
    EditText pickNumberET2;
    ArrayList<String> categoriesOfWords = new ArrayList<>();
    DateFormat dateFormat;
    String localTime;
    Date currentLocalTime;
    Calendar cal;
    int wordsToReviewCount = 0;
    Cursor wordsToReviewCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        WordsDbHelper wordsDbHelper = new WordsDbHelper(this);
        WordListContract.myWordsDB = wordsDbHelper.getWritableDatabase();
        numberPicker = (NumberPicker) findViewById(R.id.numberPicker2);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        setDividerColor(numberPicker, Color.WHITE);
        sharedPref = getSharedPreferences(AccountListContract.sharedName,MODE_PRIVATE);
        accountName = sharedPref.getString(AccountListContract.sharedName,"XD");
        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
        currentLocalTime = cal.getTime();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));
        localTime = dateFormat.format(currentLocalTime);
        startReviewBTN = (Button)findViewById(R.id.startReviewBTN);
        startReviewBTN.setVisibility(View.INVISIBLE);
        reviewCursor = WordAccountStatusContract.getWordAccountStatusCursorWithSpecificAccount(accountName);
        pickNumberET2 = (EditText)findViewById(R.id.pickNumberET2);
        pickNumberET2.setVisibility(View.INVISIBLE);
        if(reviewCursor == null) {
            Log.v("TAG", "reviewCursor jest nullem");
        }else{
            Log.v("TAG", "reviewCursor count: " + reviewCursor.getCount());
        }
        if(reviewCursor.getCount() > 0) {

            String[] wordsToReviewArray;
            String[] categoriesOfWordsToReviewArray;

                while (reviewCursor.moveToNext()) {
                    Date date = new Date();
                    try {
                        date = dateFormat.parse(reviewCursor.getString(6));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(date.before(currentLocalTime) || date.equals(currentLocalTime)){
                        Log.v("TAG","Slowo: " + reviewCursor.getString(1));
                        Log.v("TAG","Data powtorki: " + reviewCursor.getString(6));
                        Log.v("TAG","Dzisiejsza Data: " + localTime);
                        wordsToReview.add(reviewCursor.getString(1));
                        categoriesOfWordsToReview.add(reviewCursor.getString(2));
                    }else{
                        Log.v("TAG", "Nie ma slow do powrotki!! (daty niezgodne)");
                        Log.v("TAG","Slowo: " + reviewCursor.getString(1));
                        Log.v("TAG","Data powtorki: " + reviewCursor.getString(6));
                    }
                }

            wordsToReviewArray = wordsToReview.toArray(new String[wordsToReview.size()]);
            categoriesOfWordsToReviewArray = categoriesOfWordsToReview.toArray(new String[categoriesOfWordsToReview.size()]);
            wordsToReviewCursor = WordListContract.getAllWordsByArray(wordsToReviewArray, categoriesOfWordsToReviewArray);
            categoriesOfWords = new ArrayList<>();

            if(wordsToReviewCursor!=null) {
                startReviewBTN.setVisibility(View.VISIBLE);
                if (wordsToReviewCursor.getCount() > 0) {
                    if(wordsToReviewCursor.getCount() < 6){
                        pickNumberET2.setVisibility(View.VISIBLE);
                        numberPicker.setVisibility(View.INVISIBLE);
                        Toast.makeText(this,"Wprowadź cyfre z zakresu 1 do " + wordsToReviewCursor.getCount(),Toast.LENGTH_SHORT).show();
                    }else{
                        pickNumberET2.setVisibility(View.INVISIBLE);
                        numberPicker.setVisibility(View.VISIBLE);
                        numberPicker.setMinValue(1);
                        numberPicker.setMaxValue(wordsToReviewCursor.getCount());
                    }
                    while (wordsToReviewCursor.moveToNext()) {
                        germanWordsByAccount.add(wordsToReviewCursor.getString(1));
                        polishWordsByAccount.add(wordsToReviewCursor.getString(2));
                        categoriesOfWords.add(wordsToReviewCursor.getString(3));
                    }
                } else {
                    numberPicker.setVisibility(View.INVISIBLE);
                    startReviewBTN.setVisibility(View.INVISIBLE);
                    Toast.makeText(this,"Niewystarczająca ilość słów do powtórki!",Toast.LENGTH_SHORT).show();
                }
            }else{
                Log.v("TAG", "Nie ma słów do powtórki!");
                numberPicker.setVisibility(View.INVISIBLE);
                startReviewBTN.setVisibility(View.INVISIBLE);
                startReviewBTN.setVisibility(View.INVISIBLE);
                Toast.makeText(this,"Niewystarczająca ilość słów do powtórki!",Toast.LENGTH_SHORT).show();
            }
        }else{
            numberPicker.setVisibility(View.INVISIBLE);
            startReviewBTN.setVisibility(View.INVISIBLE);
            Toast.makeText(this,"Niewystarczająca ilość słów do powtórki!",Toast.LENGTH_SHORT).show();
        }
    }

    private void setDividerColor(NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException | Resources.NotFoundException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public void onStartReview(View view) {
        if(wordsToReviewCursor.getCount() < 6){
            if(!pickNumberET2.getText().toString().equals("")) {
                wordsToReviewCount = Integer.parseInt(pickNumberET2.getText().toString());
            }else{
                Toast.makeText(this,"Wprowadź cyfre z zakresu 1 do " + reviewCursor.getCount(),Toast.LENGTH_SHORT).show();
            }
        }else{
            wordsToReviewCount = numberPicker.getValue();
        }

        if((wordsToReviewCount <= wordsToReviewCursor.getCount()) && wordsToReviewCount!=0) {
            Intent myIntent = new Intent(getApplicationContext(), LearningToPolishActivity.class);
                myIntent.putStringArrayListExtra("german_words", germanWordsByAccount);
                myIntent.putStringArrayListExtra("polish_words", polishWordsByAccount);
                myIntent.putStringArrayListExtra("word_category", categoriesOfWords);
                myIntent.putExtra("words_to_review_count", wordsToReviewCount);
                startActivityForResult(myIntent, 1);
        }else{
            Toast.makeText(this,"Wprowadź cyfre z zakresu 1 do " + reviewCursor.getCount(),Toast.LENGTH_SHORT).show();
            pickNumberET2.setText("");
        }
    }
}
