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
import android.widget.Toast;

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
    Button obtainNumberBTN;
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
        obtainNumberBTN = (Button)findViewById(R.id.checkNumberBTN);
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
                obtainNumberBTN.setVisibility(View.VISIBLE);
                TextView ed = (TextView) view.findViewById(android.R.id.text1);
                categoryName = ed.getText().toString();
                chooseCategoryTV.setText("Chosen Category: " + categoryName);
            }
        });
    }



    public void obtainWordsToLearn(){
        allPolishWordsInCategoryArrayList.clear();
        allGermanWordsInCategoryArrayList.clear();
        polishWordsLearnedArrayList.clear();
        germanWordsLearnedArrayList.clear();
        categoriesOfWordsArrayList.clear();

        Cursor wordsInCategory = WordListContract.getWordsInCategory(categoryName);
        while(wordsInCategory.moveToNext()) {
            allGermanWordsInCategoryArrayList.add(wordsInCategory.getString(0));
            allPolishWordsInCategoryArrayList.add(wordsInCategory.getString(1));
        }
        Cursor polishLearnedWordsCursor = WordAccountStatusContract.getLearnedPolishWords(accountName);
        Log.v("TAG", "polishLearnedWordsCursor size: " + polishLearnedWordsCursor.getCount());
        while(polishLearnedWordsCursor.moveToNext()) {
            Log.v("TAG", "wordsInCategory.getString(0): " + polishLearnedWordsCursor.getString(0));
            polishWordsLearnedArrayList.add(polishLearnedWordsCursor.getString(0));
        }
        //for(int i=0;i<categoriesOfWordsArrayList.size();i++){
         //   Log.v("TAG","categoriesOfWordsArrayList!@!@!@!@: " + categoriesOfWordsArrayList.get(i));
        //}


        for(int i=0; i<polishWordsLearnedArrayList.size(); i++){
            Log.v("TAG", "Słowo: " + i + " -- " + polishWordsLearnedArrayList.get(i));
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
        for(int i=0;i<germanWordsToLearnArray.size();i++){
            categoriesOfWordsArrayList.add(categoryName);
            Log.v("TAG","germanWordsToLearnArray: " + germanWordsToLearnArray.get(i));
        }
        for(int i=0;i<polishWordsToLearnArray.size();i++){
            Log.v("TAG","polishWordsToLearnArray: " + polishWordsToLearnArray.get(i));
        }

       // Log.v("TAG","germanWordsToLearnArray size: " + germanWordsToLearnArray.size());
       // Log.v("TAG","polishWordsToLearnArray size: " + polishWordsToLearnArray.size());
    }

    public void startLearningActivity(View view) {
        selectedCategoryCount = numberPicker.getValue();
        if(selectedCategoryCount < 4){
            Toast.makeText(this,"Za mało słów do nauki, wybierz inną kategorię.",Toast.LENGTH_SHORT).show();
        }else {
            Intent myIntent = new Intent(getApplicationContext(), LearningToPolishActivity.class);
            myIntent.putExtra("category_name", categoryName);
            myIntent.putExtra("category_count", selectedCategoryCount);
            myIntent.putStringArrayListExtra("german_words", germanWordsToLearnArray);
            myIntent.putStringArrayListExtra("polish_words", polishWordsToLearnArray);
            myIntent.putStringArrayListExtra("word_category", categoriesOfWordsArrayList);
            Log.v("TAG","germanWordsToLearnArray: "+germanWordsToLearnArray.size() + " polishWordsToLearnArray: " + polishWordsToLearnArray.size() + " categoriesOfWordsArrayList.size(): " + categoriesOfWordsArrayList.size());
            startActivityForResult(myIntent, 1);
        }
        //TODO Sprawdzic czy to ma prawo działać i ew dodać wydobycie do LearningToPolishActivity (na pewno trza posprawdzać arraye)
    }

    public void onCheckNumberBTNClick(View view) {
        obtainWordsToLearn();
        if(germanWordsToLearnArray.size() > 4) {
            numberPicker.setMinValue(4);
            numberPicker.setMaxValue(germanWordsToLearnArray.size());
        }else {
            numberPicker.setValue(0);
        }
        obtainNumberBTN.setVisibility(View.INVISIBLE);
        categoryCountBTN.setVisibility(View.VISIBLE);
        Log.v("TAG","selectedCategoryCount: " + selectedCategoryCount);
    }
}


