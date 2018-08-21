package com.example.loren.altklausurenneu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewclass extends AppCompatActivity {

    private static final String TAG = "WebViewClass";
    private static final String DOWNLOAD_URL_BUNDLE = "downloadurl";
    WebView webView;
    ProgressDialog progressDialog;
    private String URL;


    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Log.d(TAG,"Created with URL: "+ getURL());

        init();
        listener();
    }


    /**
     * Initializes the Webview with settings
     * Settings for ProgressDialog
     * loads URL in Google Docs WebView
     */
    private void init(){
        webView = (WebView)findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        //ProgressDialog

        progressDialog = new ProgressDialog(WebViewclass.this);
        progressDialog.setTitle("PDF");
        progressDialog.setMessage("Lädt...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);

        String url = getIntent().getExtras().getString(DOWNLOAD_URL_BUNDLE);
        Log.d(TAG,"Url from Intent: "+url);
        String urlneu = "https://firebasestorage.googleapis.com/v0/b/altklausuren-31811.appspot.com/o/3194?alt=media&token=7930b85a-8fe9-4f56-b2d2-b2806e1c0b19";

        //Example URL from PDF, loads
        webView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url="+urlneu);

    }

    /**
     * Sets WebView Client
     * onPage started -> Progress shows
     * onPage finished -> Progress dismisses
     */
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
