package com.example.blazej.languagelearner;

import android.util.Log;

import com.example.blazej.languagelearner.data.WordListContract;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * Created by Blazej on 19.10.2017.
 */

class DownloadTask {
    static ArrayList<String> downloadHeadingName(String url, String htmlContent){
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements links;
        if (doc != null) {
            links = doc.select(htmlContent);
        }else{
            return null;
        }
        String allTitlesString = links.html();
        String[] myArray = allTitlesString.split("\n");
        return new ArrayList<>(Arrays.asList(myArray));
    }

    static ArrayList<LinkedHashMap<String,String>> downloadAllWords(String url, String htmlContent, String baseSiteUrl, String wordsTables){
        ArrayList<LinkedHashMap<String,String>> finalList = new ArrayList<>();
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements links = null;
        if (doc != null) {
            links = doc.select(htmlContent);
        }
        ArrayList<String> linksList = new ArrayList<>();
        if (links != null) {
            for (Element link : links) {
                linksList.add(baseSiteUrl + link.attr("href"));
            }
        }else{
            return null;
        }
        Document tempDoc = null;
        for (int i = 0; i < linksList.size(); i++) {
            try {
                tempDoc = Jsoup.connect(linksList.get(i)).get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Elements words;
            if (tempDoc != null) {
                words = tempDoc.select(wordsTables);
            }else{
                return null;
            }

            LinkedHashMap<String, String> tempMap = new LinkedHashMap<>();
            for (Element word : words) {
                Element germanWord = word.child(0);
                Element polishWord = word.child(1);
                String germanWordString = germanWord.html();
                String polishWordString = polishWord.html();
                tempMap.put(germanWordString, polishWordString);
            }
            finalList.add(tempMap);
        }
        return finalList;
    }
}
