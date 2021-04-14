package com.pmvc.conquistapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public class MyChromeClient extends WebChromeClient {

    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    protected FrameLayout mFullscreenContainer;
    private int mOriginalOrientation;
    private int mOriginalSystemUiVisibility;

    private ProgressBar progressBar;
    private Activity activity;
    private Context context;

    public MyChromeClient(Activity act, ProgressBar pb) {
        progressBar = pb;
        activity = act;
        context = activity.getApplicationContext();
    }

    public Bitmap getDefaultVideoPoster() {
        if (mCustomView == null) {
            return null;
        }
        return BitmapFactory.decodeResource(context.getResources(), 2130837573);
    }

    @Override
    public void onProgressChanged(WebView view, int progress) {
        progressBar.setProgress(progress);
        if (progress == 100) {
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void onHideCustomView()
    {
        ((FrameLayout)activity.getWindow().getDecorView()).removeView(this.mCustomView);
        this.mCustomView = null;
        activity.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
        activity.setRequestedOrientation(this.mOriginalOrientation);
        this.mCustomViewCallback.onCustomViewHidden();
        this.mCustomViewCallback = null;
    }

    @Override
    public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
    {
        if (this.mCustomView != null)
        {
            onHideCustomView();
            return;
        }
        this.mCustomView = paramView;
        this.mOriginalSystemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
        this.mOriginalOrientation = activity.getRequestedOrientation();
        this.mCustomViewCallback = paramCustomViewCallback;
        ((FrameLayout)activity.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
        activity.getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }
}
