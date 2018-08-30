package com.example.loren.altklausurenneu;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;


import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.loren.altklausurenneu.PdfViewer.TAG;

public class PdfViewer extends AppCompatActivity {

    PDFView mpdfViewer;
    String url;
     final static String TAG = "PdfViewer";
    private static final String BUNDLE_DOWNLOAD_URL = "url";
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        mpdfViewer = (PDFView)findViewById(R.id.pdfView);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            //get url from MainActivity
            url = getIntent().getStringExtra(BUNDLE_DOWNLOAD_URL);
            Log.d(TAG, "DownloadUrl: "+url);
        }
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        new RetrievePDFStream().execute(url);



    }
    //todo add progress circle

    class RetrievePDFStream extends AsyncTask<String, Void, InputStream> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                //get url from array
                URL url = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                //open if status is OK
                if (httpURLConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                }
            }catch (IOException e){
                Log.d(TAG,"Could not get Input Stream: "+ e.getMessage());
                return null;

            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            //load pdf from stream
            mpdfViewer.fromStream(inputStream).load();
            progressBar.setVisibility(View.GONE);
        }
    }
}


