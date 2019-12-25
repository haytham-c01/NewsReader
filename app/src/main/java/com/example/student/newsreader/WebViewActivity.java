package com.example.student.newsreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        WebView webView= findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());

        String webContent= getIntent().getStringExtra(MainActivity.EXTRA_WEB_CONTENT);
        webView.loadData(webContent, "text/html", "UTF-8");

    }
}
