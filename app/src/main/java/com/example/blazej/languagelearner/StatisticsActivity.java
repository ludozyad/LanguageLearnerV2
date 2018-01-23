package com.example.blazej.languagelearner;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.blazej.languagelearner.data.AccountListContract;
import com.example.blazej.languagelearner.data.WordAccountStatusContract;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    String accountName;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    ArrayList<Integer> goodAnss = new ArrayList<>();
    ArrayList<Integer> badAnss = new ArrayList<>();
    TextView bestDayTV;
    TextView bestScoreTV;
    TextView worstDayTV;
    TextView worstScoreTV;
    TextView worstWordTV;
    GraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        sharedPref = getSharedPreferences(AccountListContract.sharedName,MODE_PRIVATE);
        accountName = sharedPref.getString(AccountListContract.sharedName,"");
        bestDayTV = (TextView)findViewById(R.id.bestDayTV);
        bestDayTV.setVisibility(View.VISIBLE);
        bestScoreTV = (TextView)findViewById(R.id.bestScoreTV);
        bestScoreTV.setVisibility(View.VISIBLE);
        worstDayTV = (TextView)findViewById(R.id.worstDayTV);
        worstDayTV.setVisibility(View.VISIBLE);
        worstScoreTV = (TextView)findViewById(R.id.worstScoreTV);
        worstScoreTV.setVisibility(View.VISIBLE);
        worstWordTV = (TextView)findViewById(R.id.worstWordTV);
        worstWordTV.setVisibility(View.VISIBLE);
        graph = (GraphView) findViewById(R.id.statisticGraph);
        graph.setVisibility(View.VISIBLE);

        Cursor notLearnedCursor = WordAccountStatusContract.myIsLearnedDB.rawQuery(
                "SELECT COUNT(" + WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_LEARNED_FORGOT_DATE + ")," +
                        WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_LEARNED_FORGOT_DATE  + ", " +
                        WordAccountStatusContract.WordStatisticColumnsEntry.POLISH_COLUMN_NAME + " FROM " +
                        WordAccountStatusContract.WordStatisticColumnsEntry.TABLE_NAME +
                        " WHERE "  + WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_IS_LEARNED + " = 0" + " AND " +
                        WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_ACCOUNT_NAME + " = "  + "'" + accountName + "'" +
                        " GROUP BY " +  WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_LEARNED_FORGOT_DATE, null);

        Cursor allUnlearnedWordsCursor = WordAccountStatusContract.myIsLearnedDB.rawQuery( "SELECT " +
                        WordAccountStatusContract.WordStatisticColumnsEntry.POLISH_COLUMN_NAME + " FROM " +
                        WordAccountStatusContract.WordStatisticColumnsEntry.TABLE_NAME +
                        " WHERE "  + WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_IS_LEARNED + " = 0" + " AND " +
                        WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_ACCOUNT_NAME + " = "  + "'" + accountName + "'" , null);

        Cursor learnedCursor = WordAccountStatusContract.myIsLearnedDB.rawQuery(
                "SELECT COUNT(" + WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_LEARNED_FORGOT_DATE + ")," +
                        WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_LEARNED_FORGOT_DATE  + ", " +
                        WordAccountStatusContract.WordStatisticColumnsEntry.POLISH_COLUMN_NAME + " FROM " +
                        WordAccountStatusContract.WordStatisticColumnsEntry.TABLE_NAME +
                        " WHERE "  + WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_IS_LEARNED + " = 1" + " AND " +
                        WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_ACCOUNT_NAME + " = "  + "'" + accountName + "'" +
                        " GROUP BY " +  WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_LEARNED_FORGOT_DATE, null);

        Cursor getAllDatesCursor = WordAccountStatusContract.myIsLearnedDB.rawQuery(
                "SELECT " + WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_LEARNED_FORGOT_DATE + " FROM " +
                WordAccountStatusContract.WordStatisticColumnsEntry.TABLE_NAME + " WHERE " +
                        WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_ACCOUNT_NAME + " = "  + "'" + accountName + "'"
                        + " GROUP BY " + WordAccountStatusContract.WordStatisticColumnsEntry.COLUMN_LEARNED_FORGOT_DATE, null);




        ArrayList<String> allUnlearnedWords = new ArrayList<>();
        while(allUnlearnedWordsCursor.moveToNext()){
            allUnlearnedWords.add(allUnlearnedWordsCursor.getString(0));
        }

        String worstWord = "";
        Map<String, Integer> stringsCount = new HashMap<>();
        for(String s: allUnlearnedWords)
        {
            Integer c = stringsCount.get(s);
            if(c == null) c = new Integer(0);
            c++;
            stringsCount.put(s,c);
        }
        Map.Entry<String,Integer> mostRepeated = null;
        for(Map.Entry<String, Integer> e: stringsCount.entrySet())
        {
            if(mostRepeated == null || mostRepeated.getValue()<e.getValue())
                mostRepeated = e;
        }
        if(mostRepeated!=null){
            worstWord = mostRepeated.getKey();
            Log.v("TAG", "mostRepeated: " + mostRepeated.getValue());
        }

        ArrayList<String> allDates = new ArrayList<>();
        ArrayList<Integer> allBadAns = new ArrayList<>();
        ArrayList<Integer> allGoodAns = new ArrayList<>();
        while(getAllDatesCursor.moveToNext()){
            allDates.add(getAllDatesCursor.getString(0));
            Log.v("TAG","data: " + getAllDatesCursor.getString(0));
        }
        while(learnedCursor.moveToNext()){
            allGoodAns.add(learnedCursor.getInt(0));
            Log.v("TAG","dobra: " + learnedCursor.getString(0));
        }
        while(notLearnedCursor.moveToNext()){
            allBadAns.add(notLearnedCursor.getInt(0));
            Log.v("TAG","zla: " + notLearnedCursor.getString(0));
        }

        for(int i=0; i<allDates.size(); i++){
           if(allGoodAns.size() > 0) {
               if (allGoodAns.size() == i) {
                   goodAnss.add(0);
               }else{
                   goodAnss.add(allGoodAns.get(i));
               }
           }else{
               goodAnss.add(0);
           }
        }

        for(int i=0; i<allDates.size(); i++){
            if(allBadAns.size() > 0) {
                if (allBadAns.size() == i) {
                    badAnss.add(0);
                }else{
                    badAnss.add(allBadAns.get(i));
                }
            }else{
                badAnss.add(0);
            }
        }

        if(getAllDatesCursor.getCount() > 0) {
            if(allBadAns.size() == 0){
                worstWordTV.setVisibility(View.INVISIBLE);
            }

            worstWordTV.setText(getString(R.string.your_worst_word, worstWord));
            BarGraphSeries<DataPoint> series = new BarGraphSeries<>();
            BarGraphSeries<DataPoint> series2 = new BarGraphSeries<>();
            ArrayList<Date> dateArrayList = new ArrayList<>();
            for (int i = 0; i < allDates.size(); i++) {
                try {
                    dateArrayList.add(dateFormat.parse(allDates.get(i)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            int badAnsMax = Collections.max(badAnss);
            int goodAnsMax = Collections.max(goodAnss);
            int goodAnsMaxIndex = goodAnss.indexOf(Collections.max(goodAnss));
            String bestDate = allDates.get(goodAnsMaxIndex);
            int badAnsMaxIndex = badAnss.indexOf(Collections.max(badAnss));
            String worstDate = allDates.get(badAnsMaxIndex);
            bestDayTV.setText(getString(R.string.your_best_day, bestDate));
            bestScoreTV.setText(getString(R.string.your_best_score, goodAnsMax));
            worstDayTV.setText(getString(R.string.your_worst_day, worstDate));
            worstScoreTV.setText(getString(R.string.your_worst_score, badAnsMax));
            Collections.sort(dateArrayList);

            for (int i = 0; i < badAnss.size(); i++) {
                try {
                    series.appendData(new DataPoint(dateFormat.parse(allDates.get(i)), -badAnss.get(i)), true, badAnss.size());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < goodAnss.size(); i++) {
                try {
                    series2.appendData(new DataPoint(dateFormat.parse(allDates.get(i)), goodAnss.get(i)), true, goodAnss.size());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateArrayList.get(0));
            cal.add(Calendar.DATE, -14);
            Date dateBefore = cal.getTime();
            cal.setTime(dateArrayList.get(dateArrayList.size() - 1));
            cal.add(Calendar.DATE, +14);
            Date dateAfter = cal.getTime();

            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getGridLabelRenderer().setHumanRounding(false);
            graph.getViewport().setMinX(dateBefore.getTime());
            graph.getViewport().setMaxX(dateAfter.getTime());
            graph.getViewport().setMinY(-badAnsMax - 1);
            graph.getViewport().setMaxY(goodAnsMax + 1);
            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getApplicationContext()));
            graph.getGridLabelRenderer().setNumHorizontalLabels(3);
            series.setSpacing(40);
            series2.setSpacing(40);
            series.setDrawValuesOnTop(true);
            series2.setDrawValuesOnTop(true);
            series.setValuesOnTopColor(Color.BLACK);
            series.setColor(Color.rgb(205, 53, 34));
            series2.setValuesOnTopColor(Color.BLACK);
            series2.setColor(Color.rgb(67, 156, 0));
            graph.addSeries(series);
            graph.addSeries(series2);
            notLearnedCursor.close();
            learnedCursor.close();
            getAllDatesCursor.close();
        }else{
            Toast.makeText(this,"Brak wystarczających danych do wyświetlenia statystyk",Toast.LENGTH_SHORT).show();
            bestDayTV.setVisibility(View.INVISIBLE);
            bestScoreTV.setVisibility(View.INVISIBLE);
            worstDayTV.setVisibility(View.INVISIBLE);
            worstScoreTV.setVisibility(View.INVISIBLE);
            worstWordTV.setVisibility(View.INVISIBLE);
            graph.setVisibility(View.INVISIBLE);
        }
    }
}
