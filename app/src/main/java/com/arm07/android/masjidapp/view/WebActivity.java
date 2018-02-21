package com.arm07.android.masjidapp.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.arm07.android.masjidapp.R;

public class WebActivity extends AppCompatActivity {

    TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        mTextView=findViewById(R.id.textView);
        initWebView();
    }

    private void initWebView() {
        WebView webView=(WebView)findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://www.google.com");
    }
}
