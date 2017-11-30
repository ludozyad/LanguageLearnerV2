package com.example.blazej.languagelearner;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChooseGrammarCategoryActivity extends AppCompatActivity {

    TextView chooseCategoryTV;
    ListView grammarCategoriesListView;
    String[] categoriesArrayList;
    String categoryName;
    ArrayList<Integer> tempArrayListOfResources = new ArrayList<>();
    int [] categoriesCountArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_grammar_category);
        chooseCategoryTV = (TextView)findViewById(R.id.chooseGrammarCategoryTV);
        grammarCategoriesListView = (ListView)findViewById(R.id.grammarList);
        addCategories();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, categoriesArrayList) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextAppearance(R.style.learning_font);
                text.setTextColor(Color.rgb(27,100,255));
                text.setTextSize(19);
                return view;
            }
        };
        grammarCategoriesListView.setAdapter(adapter);



        grammarCategoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView ed = (TextView) view.findViewById(android.R.id.text1);
                categoryName = ed.getText().toString();
                tempArrayListOfResources.clear();
                int  categoryId = position;
                Intent intent  = new Intent(getApplicationContext(),WebViewActivity.class);
                intent.putExtra("resources_array_list",tempArrayListOfResources);
                intent.putExtra("resource_name",mResources[categoryId]);
                startActivity(intent);
            }
        });
    }


    String[] mResources = {
            "g1.png",
            "g2.png",
            "g3.png",
            "g4.png",
            "g5.png",
            "g6.png",
            "g7.png",
            "g8.png",
            "g9.png",
            "g10.png",
            "g11.png",
            "g12.png",
            "g13.png",
            "g14.png",
            "g15.png",
    };
    public void addCategories(){
        categoriesArrayList = new String[]{
                "Rzeczownik",
                "Rodzajnik",
                "Zaimek",
                "Liczebnik",
                "Przymiotnik",
                "Przysłówek",
                "Czasownik",
                "Czas przeszły Perfekt",
                "Czas przeszły Imperfekt",
                "Czas przyszły Futur I/II",
                "Tryb rozkazujący Imperativ",
                "Tryb przypuszczający Konjuktiv II",
                "Lista czasowników nieregularnych",
                "Zasady wymowy niemieckiej",
                "Nowa pisownia niemiecka"

        };
    }
}
