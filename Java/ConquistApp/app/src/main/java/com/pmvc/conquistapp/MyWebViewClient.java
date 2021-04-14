package com.pmvc.conquistapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Arrays;
import java.util.List;

public class MyWebViewClient extends WebViewClient {

    private ProgressBar progressBar;
    private Activity activity;
    private SwipeRefreshLayout swipe;


    private Context context;
    private String webViewTitle = "";

    public MyWebViewClient() {}

    public MyWebViewClient(Activity act, ProgressBar pb, SwipeRefreshLayout srl) {
        activity = act;
        progressBar = pb;
        swipe = srl;
        context = activity.getApplicationContext();
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        progressBar.setVisibility(View.VISIBLE);

    }


    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        //view.stopLoading();
        Toast toast = Toast.makeText(context, "Sem conexão com a internet!", Toast.LENGTH_SHORT);
        toast.show();
        //view.loadUrl("file:///android_asset/error.html");
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        progressBar.setVisibility(View.GONE);
        swipe.setRefreshing(false);
        if (view.getTitle() != null && view.getTitle().length() > 0) {
            webViewTitle = view.getTitle();
        }

//                        getSupportActionBar().setTitle(actionBarTitle);
        ((AppCompatActivity)activity).getSupportActionBar().setSubtitle(webViewTitle);
    }

    //List<String> whiteHosts = Arrays.asList("stackoverflow.com",  "stackexchange.com", "google.com");
    List<String> whiteHosts = Arrays.asList("aprendendosempre.org", "smed.pmvc.ba.gov.br");

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        String host = Uri.parse(url).getHost();
        if (whiteHosts.contains(host)) {
            return false;
        }

        view.loadUrl("smed.pmvc.ba.gov.br/estudoremoto/login-control");
        return true;
    }
}
