package com.example.blazej.languagelearner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.blazej.languagelearner.data.AccountListContract;
import com.example.blazej.languagelearner.data.WordAccountStatusContract;
import com.example.blazej.languagelearner.data.WordAccountStatusDbHelper;
import com.example.blazej.languagelearner.data.WordListContract;
import com.example.blazej.languagelearner.data.WordsDbHelper;

import java.util.ArrayList;

public class ChooseCategoryActivity extends AppCompatActivity {


    ArrayList<String> allGermanWordsInCategoryArrayList = new ArrayList<>();
    ArrayList<String> allPolishWordsInCategoryArrayList = new ArrayList<>();
    ArrayList<String> germanWordsLearnedArrayList = new ArrayList<>();
    ArrayList<String> polishWordsLearnedArrayList = new ArrayList<>();
    ArrayList<String> germanWordsToLearnArray;
    ArrayList<String> polishWordsToLearnArray;
    ArrayList<String> categoriesOfWordsArrayList = new ArrayList<>();
    ListView categoriesListView;
    NumberPicker numberPicker;
    TextView chooseCategoryTV;
    String categoryName;
    int selectedCategoryCount;
    Button categoryCountBTN;
    String accountName;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);
        sharedPref = getSharedPreferences(AccountListContract.sharedName,MODE_PRIVATE);
        accountName = sharedPref.getString(AccountListContract.sharedName,"XD");
        chooseCategoryTV = (TextView)findViewById(R.id.chooseCategoryTV);
        categoriesListView = (ListView) findViewById(R.id.categoriesListView);
        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        categoryCountBTN = (Button)findViewById(R.id.startLearningActivityBTN);
        categoryCountBTN.setVisibility(View.INVISIBLE);

        Cursor myCategories = WordListContract.getAllCategories();

        ArrayList<String> categoriesNames = new ArrayList<>();
        while (myCategories.moveToNext()) {
            categoriesNames.add(myCategories.getString(0));
        }
        String[] myCategoriesArray = categoriesNames.toArray(new String[categoriesNames.size()]);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, myCategoriesArray);
        categoriesListView.setAdapter(adapter);
        categoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView ed = (TextView) view.findViewById(android.R.id.text1);
                categoryName = ed.getText().toString();
                chooseCategoryTV.setText("Chosen Category: " + categoryName);
                numberPicker.setMinValue(4);
                numberPicker.setMaxValue(WordListContract.getCategoryCount(categoryName));
                selectedCategoryCount = numberPicker.getValue();
                Log.v("TAG","selectedCategoryCount: " + selectedCategoryCount);
                categoryCountBTN.setVisibility(View.VISIBLE);
            }
        });
    }



    public void obtainWordsToLearn(){
        Cursor wordsInCategory = WordListContract.getWordsInCategory(categoryName);
        while(wordsInCategory.moveToNext()) {
            allGermanWordsInCategoryArrayList.add(wordsInCategory.getString(0));
            allPolishWordsInCategoryArrayList.add(wordsInCategory.getString(1));
        }
        Cursor polishLearnedWordsCursor = WordAccountStatusContract.getLearnedPolishWords(accountName);
        while(wordsInCategory.moveToNext()) {
            polishWordsLearnedArrayList.add(wordsInCategory.getString(0));
            categoriesOfWordsArrayList.add(categoryName);
        }
        String[] polishWordsLearnedArray = new String[polishLearnedWordsCursor.getCount()];
        String[] categoriesOfWordsArray = new String[polishLearnedWordsCursor.getCount()];

        if(polishLearnedWordsCursor.getCount() > 0) {
            polishWordsLearnedArray = polishWordsLearnedArrayList.toArray(new String[polishWordsLearnedArrayList.size()]);
            categoriesOfWordsArray = categoriesOfWordsArrayList.toArray(new String[categoriesOfWordsArrayList.size()]);
        }else{
            Log.v("TAG", "reviewCursor pusty!!");
        }
       // Log.v("TAG", "polishWordsLearnedArray size: " + polishWordsLearnedArray.length);

        Cursor allLearnedWordsInThisAccount = WordListContract.getAllWordsByArray(polishWordsLearnedArray,categoriesOfWordsArray); // tu zjebane cos
        //Log.v("TAG","allLearnedWordsInThisAccount size: " + allLearnedWordsInThisAccount.getCount());
        if(allLearnedWordsInThisAccount != null) {
            while (allLearnedWordsInThisAccount.moveToNext()) {
                germanWordsLearnedArrayList.add(allLearnedWordsInThisAccount.getString(1));
            }
        }
        //Log.v("TAG","germanWordsLearnedArrayList size: " + germanWordsLearnedArrayList.size());
        allGermanWordsInCategoryArrayList.removeAll(germanWordsLearnedArrayList);
        allPolishWordsInCategoryArrayList.removeAll(polishWordsLearnedArrayList);
        germanWordsToLearnArray = allGermanWordsInCategoryArrayList;
        polishWordsToLearnArray = allPolishWordsInCategoryArrayList;
       // Log.v("TAG","germanWordsToLearnArray size: " + germanWordsToLearnArray.size());
       // Log.v("TAG","polishWordsToLearnArray size: " + polishWordsToLearnArray.size());
    }

    public void startLearningActivity(View view) {
        obtainWordsToLearn();
        Intent myIntent = new Intent(getApplicationContext(), LearningToPolishActivity.class);
        myIntent.putExtra("category_name", categoryName);
        myIntent.putExtra("category_count", selectedCategoryCount);
        myIntent.putStringArrayListExtra("german_words",germanWordsToLearnArray);
        myIntent.putStringArrayListExtra("polish_words",polishWordsToLearnArray);
        startActivityForResult(myIntent,1);
        //TODO Sprawdzic czy to ma prawo działać i ew dodać wydobycie do LearningToPolishActivity (na pewno trza posprawdzać arraye)
    }
}


