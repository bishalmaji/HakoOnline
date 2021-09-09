package com.hako.dreamproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.hako.dreamproject.utils.AppController;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        WebView webView = findViewById(R.id.webtest);
//        String playerName = getIntent().getStringExtra("playerName");
//        String  playerImg = getIntent().getStringExtra("playerImg");
//        String myName = AppController.getInstance().sharedPref.getString("sname","Me");


        // Configure the webview so that the game will load
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);

        // Load in the game's HTML file
        webView.loadUrl("file:///android_asset/index.html");

    }
}