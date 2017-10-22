package com.example.blazej.languagelearner;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.blazej.languagelearner.data.WordListContract;
import com.example.blazej.languagelearner.data.WordsDbHelper;

import java.util.ArrayList;

public class ChooseCategoryActivity extends AppCompatActivity {
    public String selectedCategory = "category";
    SQLiteDatabase myWordDB;
    ListView categoriesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);
        categoriesListView = (ListView) findViewById(R.id.categoriesListView);
        WordsDbHelper myDBHelper = new WordsDbHelper(this);
        myWordDB = myDBHelper.getWritableDatabase();
        Cursor myCategories = getAllCategories();
        ArrayList<String> categoriesNames = new ArrayList<>();
        while (myCategories.moveToNext()) {
            categoriesNames.add(myCategories.getString(0));
        }
        String[] myCategoriesArray = categoriesNames.toArray(new String[categoriesNames.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, myCategoriesArray);
        categoriesListView.setAdapter(adapter);
        categoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(getApplicationContext(), LearningToPolishActivity.class);
                TextView ed = (TextView) view.findViewById(android.R.id.text1);
                String category = ed.getText().toString();
                myIntent.putExtra(selectedCategory, category);
                startActivity(myIntent);
            }
        });
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
}


