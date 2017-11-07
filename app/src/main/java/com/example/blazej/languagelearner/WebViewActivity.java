package com.example.blazej.languagelearner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;


public class WebViewActivity extends AppCompatActivity {
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Intent intent = getIntent();
        webView = (WebView)findViewById(R.id.webView);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptEnabled(false);
        String resource = intent.getStringExtra("resource_name");
        String htmlString = "<html><body><img src=\"" + " " + resource + "\" width=\"100%\"></body></html>";
        webView.loadDataWithBaseURL("file:///android_asset/", htmlString, "text/html", "UTF-8", null);
    }
}
