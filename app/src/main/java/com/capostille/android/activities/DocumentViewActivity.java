package com.capostille.android.activities;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.capostille.android.R;

public class DocumentViewActivity extends AppCompatActivity {

    WebView webView;
    String url_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        webView = findViewById(R.id.webView);
//                webView.webViewClient = WebViewClient()
//        webView.settings.setSupportZoom(true)
//        webView.settings.javaScriptEnabled = true
        Bundle b= getIntent().getExtras();
        if (b != null) {
            url_= b.getString("url");
        }
       // ProgressDialog pd= ApiConstants.showProgressDialog(this,"Please wait....");
       // pd.show();
        //webView.setWebViewClient(new WebViewClient());
        final ProgressDialog pd = ProgressDialog.show(DocumentViewActivity.this, "", "Please wait, your transaction is being processed...", true);
        WebSettings webSettings= webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        //webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);

        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(DocumentViewActivity.this, description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                pd.show();
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                pd.dismiss();

                String webUrl = webView.getUrl();

            }


    });


        String url = "https://github.github.com/training-kit/downloads/github-git-cheat-sheet.pdf";
        webView.loadUrl("https://docs.google.com/gview?embedded=true&url="+url_);
       // webView.loadUrl("www.google.com");
       // pd.dismiss();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
