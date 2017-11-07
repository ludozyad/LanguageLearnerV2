package com.example.blazej.languagelearner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
    ArrayList<String> categoriesOfLearnedWordsArrayList = new ArrayList<>();
    ArrayList<String> germanWordsToLearnArray;
    ArrayList<String> polishWordsToLearnArray;
    ArrayList<String> categoriesOfWordsArrayList = new ArrayList<>();
    TextView myTextView;
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
        chooseCategoryTV = (TextView)findViewById(R.id.chooseCategoryTV);
        categoryName = "";
        chooseCategoryTV.setText(getString(R.string.chosen_category,categoryName));
        sharedPref = getSharedPreferences(AccountListContract.sharedName,MODE_PRIVATE);
        accountName = sharedPref.getString(AccountListContract.sharedName,"XD");
        categoriesListView = (ListView) findViewById(R.id.categoriesListView);
        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        setDividerColor(numberPicker, Color.WHITE);
        categoryCountBTN = (Button)findViewById(R.id.startLearningActivityBTN);
        categoryCountBTN.setVisibility(View.INVISIBLE);
        Cursor myCategories = WordListContract.getAllCategories();

        ArrayList<String> categoriesNames = new ArrayList<>();
        while (myCategories.moveToNext()) {
            categoriesNames.add(myCategories.getString(0));
        }
        String[] myCategoriesArray = categoriesNames.toArray(new String[categoriesNames.size()]);
        //final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
         //       android.R.layout.simple_list_item_1, android.R.id.text1, myCategoriesArray);

        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.my_text_view, myCategoriesArray);
        //categoriesListView.setAdapter(adapter);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, myCategoriesArray) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);
                return view;
            }
        };
        categoriesListView.setAdapter(adapter);

        //categoriesListView.setAdapter(adapter);
        categoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView ed = (TextView) view.findViewById(android.R.id.text1);
                categoryName = ed.getText().toString();
                chooseCategoryTV.setText(getString(R.string.chosen_category,categoryName));

                obtainWordsToLearn();
                Log.v("TAG", "germanWordsToLearnArray size: " + germanWordsToLearnArray.size());
                Log.v("TAG", "polishWordsToLearnArray size: " + polishWordsToLearnArray.size());

                if(germanWordsToLearnArray.size() > 3) {
                    numberPicker.setMinValue(4);
                    numberPicker.setMaxValue(germanWordsToLearnArray.size());
                }else {
                    numberPicker.setMinValue(0);
                    numberPicker.setMaxValue(0);
                    numberPicker.setValue(0);
                }
                categoryCountBTN.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setDividerColor(NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public void obtainWordsToLearn() {
        allPolishWordsInCategoryArrayList.clear();
        allGermanWordsInCategoryArrayList.clear();
        polishWordsLearnedArrayList.clear();
        germanWordsLearnedArrayList.clear();
        categoriesOfWordsArrayList.clear();
        categoriesOfLearnedWordsArrayList.clear();

        Cursor polishLearnedWordsCursor = WordAccountStatusContract.getLearnedPolishWords(accountName, categoryName);
        while (polishLearnedWordsCursor.moveToNext()) {
            polishWordsLearnedArrayList.add(polishLearnedWordsCursor.getString(0));
            categoriesOfLearnedWordsArrayList.add(categoryName);
        }

        String[] polishWordsLearnedArray = new String[polishLearnedWordsCursor.getCount()];
        String[] categoriesOfLearnedWordsArray = new String[polishLearnedWordsCursor.getCount()];

        if (polishLearnedWordsCursor.getCount() > 0) {
            polishWordsLearnedArray = polishWordsLearnedArrayList.toArray(new String[polishWordsLearnedArrayList.size()]);
            categoriesOfLearnedWordsArray = categoriesOfLearnedWordsArrayList.toArray(new String[categoriesOfLearnedWordsArrayList.size()]);
        } else {
            Log.v("TAG", "reviewCursor pusty!!");
        }

        Cursor allLearnedWordsInThisAccount = WordListContract.getAllWordsByArray(polishWordsLearnedArray, categoriesOfLearnedWordsArray); // tu zjebane cos
        //Log.v("TAG","allLearnedWordsInThisAccount size: " + allLearnedWordsInThisAccount.getCount());
        if (allLearnedWordsInThisAccount != null) {
            while (allLearnedWordsInThisAccount.moveToNext()) {
                germanWordsLearnedArrayList.add(allLearnedWordsInThisAccount.getString(1));
            }
        }

        //  here we have array lists of learned words
        //  polishWordsLearnedArrayList
        //  germanWordsLearnedArrayList

        Cursor wordsInCategory = WordListContract.getWordsInCategory(categoryName);
        while (wordsInCategory.moveToNext()) {
            allGermanWordsInCategoryArrayList.add(wordsInCategory.getString(0));
            allPolishWordsInCategoryArrayList.add(wordsInCategory.getString(1));
        }

        // here we have all words in categories and learned words

        allGermanWordsInCategoryArrayList.removeAll(germanWordsLearnedArrayList);
        allPolishWordsInCategoryArrayList.removeAll(polishWordsLearnedArrayList);
        germanWordsToLearnArray = allGermanWordsInCategoryArrayList;
        polishWordsToLearnArray = allPolishWordsInCategoryArrayList;

        Log.v("TAG","germanWordsToLearnArray.size(): " + germanWordsToLearnArray.size() + "polishWordsToLearnArray.size(): " + polishWordsToLearnArray.size());

        for(int i = 0;i<germanWordsToLearnArray.size(); i++){
            Log.v("TAG", "germanWordsToLearnArray: " + germanWordsToLearnArray.get(i));
        }
        for(int i = 0;i<polishWordsToLearnArray.size(); i++){
            Log.v("TAG", "polishWordsToLearnArray: " + polishWordsToLearnArray.get(i));
        }

        int wordsToLearn = wordsInCategory.getCount() - polishLearnedWordsCursor.getCount();
        for (int i = 0; i < wordsToLearn; i++) {
            categoriesOfWordsArrayList.add(categoryName);
        }
    }

    public void startLearningActivity(View view) {
        selectedCategoryCount = numberPicker.getValue();
        if (selectedCategoryCount < 4) {
            Toast.makeText(this, "Za mało słów do nauki, wybierz inną kategorię.", Toast.LENGTH_SHORT).show();
        } else {
            Intent myIntent = new Intent(getApplicationContext(), LearningToPolishActivity.class);
            myIntent.putExtra("category_name", categoryName);
            myIntent.putExtra("category_count", selectedCategoryCount);
            myIntent.putStringArrayListExtra("german_words", germanWordsToLearnArray);
            myIntent.putStringArrayListExtra("polish_words", polishWordsToLearnArray);
            myIntent.putStringArrayListExtra("word_category", categoriesOfWordsArrayList);
            Log.v("TAG", "germanWordsToLearnArray: " + germanWordsToLearnArray.size() + " polishWordsToLearnArray: " + polishWordsToLearnArray.size() + " categoriesOfWordsArrayList.size(): " + categoriesOfWordsArrayList.size());
            startActivityForResult(myIntent, 1);
        }
    }
}


