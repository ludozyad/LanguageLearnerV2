package com.example.blazej.languagelearner;

import android.content.Intent;
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

import com.example.blazej.languagelearner.data.WordListContract;
import com.example.blazej.languagelearner.data.WordsDbHelper;

import java.util.ArrayList;

public class ChooseCategoryActivity extends AppCompatActivity {
    public String selectedCategory = "category";
    SQLiteDatabase myWordDB;
    ListView categoriesListView;
    NumberPicker numberPicker;
    TextView chooseCategoryTV;
    String category;
    int selectedCategoryCount;
    Button categoryCountBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);
        chooseCategoryTV = (TextView)findViewById(R.id.chooseCategoryTV);
        categoriesListView = (ListView) findViewById(R.id.categoriesListView);
        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        categoryCountBTN = (Button)findViewById(R.id.startLearningActivityBTN);
        categoryCountBTN.setVisibility(View.INVISIBLE);
        WordsDbHelper myDBHelper = new WordsDbHelper(this);
        myWordDB = myDBHelper.getWritableDatabase();
        Cursor myCategories = getAllCategories();
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
                category = ed.getText().toString();
                chooseCategoryTV.setText("Chosen Category: " + category);
                numberPicker.setMinValue(4);
                numberPicker.setMaxValue(getCategoryCount(category));
                selectedCategoryCount = numberPicker.getValue();
                Log.v("TAG","selectedCategoryCount: " + selectedCategoryCount);
                categoryCountBTN.setVisibility(View.VISIBLE);
            }
        });
    }


    public int getCategoryCount(String category){
        Cursor myCursor = myWordDB.query(
                WordListContract.DatabaseColumnsEntry.TABLE_NAME,
                new String[] {WordListContract.DatabaseColumnsEntry.CATEGORY_COUNT_COLUMN_NAME},
                WordListContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME + "=?",
                new String[] {category},
                null,
                null,
                null,
                "1"
        );
        myCursor.moveToFirst();
        int catCount = myCursor.getInt(0);
        myCursor.close();
        return catCount;
    }

    Cursor getAllCategories(){
        return myWordDB.query(
                WordListContract.DatabaseColumnsEntry.TABLE_NAME,
                new String[] {WordListContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME},
                null,
                null,
                WordListContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME,
                null,
                WordListContract.DatabaseColumnsEntry.CATEGORY_COLUMN_NAME
        );
    }

    public void startLearningActivity(View view) {
        Intent myIntent = new Intent(getApplicationContext(), LearningToPolishActivity.class);
        myIntent.putExtra(selectedCategory, category);
        myIntent.putExtra("category_count", selectedCategoryCount);
        startActivityForResult(myIntent,1);;
    }
}


