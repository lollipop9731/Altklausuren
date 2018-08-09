package com.example.loren.altklausurenneu;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewclass extends AppCompatActivity {

    WebView webView;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        init();
        listener();
    }

    private void init(){
        webView = (WebView)findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        progressDialog = new ProgressDialog(WebViewclass.this);
        progressDialog.setTitle("PDF");
        progressDialog.setMessage("Lädt...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        webView.loadUrl("http://docs.google.com/gview?embedded=true&url=https://www.einfach-fuer-alle.de/artikel/checkliste-barrierefreie-pdf/Checkliste-Barrierefreies-PDF.pdf");

    }

    private void listener(){
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(android.webkit.WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressDialog.show();
            }

            @Override
            public void onPageFinished(android.webkit.WebView view, String url) {
                super.onPageFinished(view, url);
                progressDialog.dismiss();
            }
        });
    }
}
