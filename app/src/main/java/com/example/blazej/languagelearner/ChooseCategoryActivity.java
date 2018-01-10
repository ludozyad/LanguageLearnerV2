package com.example.blazej.languagelearner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import com.example.blazej.languagelearner.data.AccountListContract;
import com.example.blazej.languagelearner.data.WordAccountStatusContract;
import com.example.blazej.languagelearner.data.WordListContract;
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
    TextView categoryCountTV;
    ListView categoriesListView;
    NumberPicker numberPicker;
    TextView chooseCategoryTV;
    String categoryName;
    int selectedCategoryCount;
    Button categoryCountBTN;
    EditText pickNumberET;
    String accountName;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);
        pickNumberET = (EditText)findViewById(R.id.pickNumberET);
        pickNumberET.setVisibility(View.INVISIBLE);
        chooseCategoryTV = (TextView)findViewById(R.id.chooseCategoryTV);
        categoryCountTV = (TextView)findViewById(R.id.categoryCountTV);
        categoryName = "";
        chooseCategoryTV.setText(getString(R.string.chosen_category,categoryName));
        sharedPref = getSharedPreferences(AccountListContract.sharedName,MODE_PRIVATE);
        accountName = sharedPref.getString(AccountListContract.sharedName,"XD");
        categoriesListView = (ListView) findViewById(R.id.categoriesListView);
        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker.setWrapSelectorWheel(true);
        setDividerColor(numberPicker, Color.WHITE);
        categoryCountBTN = (Button)findViewById(R.id.startLearningActivityBTN);
        categoryCountBTN.setVisibility(View.INVISIBLE);
        Cursor myCategories = WordListContract.getAllCategories();

        ArrayList<String> categoriesNames = new ArrayList<>();
        while (myCategories.moveToNext()) {
            categoriesNames.add(myCategories.getString(0));
        }
        String[] myCategoriesArray = categoriesNames.toArray(new String[categoriesNames.size()]);

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
        categoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView ed = (TextView) view.findViewById(android.R.id.text1);
                categoryName = ed.getText().toString();
                chooseCategoryTV.setText(getString(R.string.chosen_category,categoryName));

                obtainWordsToLearn();
                Log.v("TAG", "germanWordsToLearnArray size: " + germanWordsToLearnArray.size());
                Log.v("TAG", "polishWordsToLearnArray size: " + polishWordsToLearnArray.size());

                if(germanWordsToLearnArray.size() > 0) {
                    categoryCountBTN.setVisibility(View.VISIBLE);
                    chooseCategoryTV.setVisibility(View.VISIBLE);
                    categoryCountTV.setVisibility(View.VISIBLE);
                    Log.v("TAG", "germanWordsToLearnArray.size() > 0");
                    if(germanWordsToLearnArray.size()<6){
                        Toast.makeText(getApplicationContext(),"Wprowadź cyfre z zakresu 1 do " + germanWordsToLearnArray.size(),Toast.LENGTH_SHORT).show();
                        pickNumberET.setText("");
                        pickNumberET.setVisibility(View.VISIBLE);
                        numberPicker.setVisibility(View.INVISIBLE);
                    }else{
                        pickNumberET.setVisibility(View.INVISIBLE);
                        numberPicker.setVisibility(View.VISIBLE);
                        numberPicker.setMinValue(1);
                        numberPicker.setMaxValue(germanWordsToLearnArray.size());
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Za mało słów do nauki, wybierz inną kategorię.", Toast.LENGTH_SHORT).show();
                    numberPicker.setVisibility(View.INVISIBLE);
                    categoryCountBTN.setVisibility(View.INVISIBLE);
                    categoryCountTV.setVisibility(View.INVISIBLE);
                }
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
        if (allLearnedWordsInThisAccount != null) {
            while (allLearnedWordsInThisAccount.moveToNext()) {
                germanWordsLearnedArrayList.add(allLearnedWordsInThisAccount.getString(1));
            }
        }

        Cursor wordsInCategory = WordListContract.getWordsInCategory(categoryName);
        while (wordsInCategory.moveToNext()) {
            allGermanWordsInCategoryArrayList.add(wordsInCategory.getString(0));
            allPolishWordsInCategoryArrayList.add(wordsInCategory.getString(1));
        }

        allGermanWordsInCategoryArrayList.removeAll(germanWordsLearnedArrayList);
        allPolishWordsInCategoryArrayList.removeAll(polishWordsLearnedArrayList);
        germanWordsToLearnArray = allGermanWordsInCategoryArrayList;
        polishWordsToLearnArray = allPolishWordsInCategoryArrayList;

        int wordsToLearn = wordsInCategory.getCount() - polishLearnedWordsCursor.getCount();
        for (int i = 0; i < wordsToLearn; i++) {
            categoriesOfWordsArrayList.add(categoryName);
        }
    }

    public void startLearningActivity(View view) {
        if(germanWordsToLearnArray.size()> 0 && germanWordsToLearnArray.size() < 6){
            selectedCategoryCount = Integer.parseInt(pickNumberET.getText().toString());
        }else if (germanWordsToLearnArray.size() >= 6){
            selectedCategoryCount = numberPicker.getValue();
        }else{
            selectedCategoryCount=0;
        }

        if(selectedCategoryCount > 0 && selectedCategoryCount <= germanWordsToLearnArray.size()){
            Intent myIntent = new Intent(getApplicationContext(), LearningToPolishActivity.class);
            myIntent.putExtra("category_name", categoryName);
            myIntent.putExtra("category_count", selectedCategoryCount);
            myIntent.putStringArrayListExtra("german_words", germanWordsToLearnArray);
            myIntent.putStringArrayListExtra("polish_words", polishWordsToLearnArray);
            myIntent.putStringArrayListExtra("word_category", categoriesOfWordsArrayList);
            startActivityForResult(myIntent, 1);
        } else if(selectedCategoryCount > germanWordsToLearnArray.size()){
            Toast.makeText(this,"Wprowadź cyfre z zakresu 1 do " + germanWordsToLearnArray.size(),Toast.LENGTH_SHORT).show();
            pickNumberET.setText("");
        }
    }
}


