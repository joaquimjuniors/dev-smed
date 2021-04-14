package com.pmvc.conquistapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Locale;

import com.pmvc.conquistapp.R;

public class WebViewActivity extends AppCompatActivity {

    private WebView myWebView;
    private SwipeRefreshLayout swipe;
    private ProgressBar progressBar;

    private String webViewTitle = "";
    private String url = "";

    private static final int PERMISSION_REQUEST_CODE = 1;
    private MyDownloadListener myDownloadListener;
    String atvName;

    @SuppressLint("SetJavaScriptEnable")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().requestFeature(Window.FEATURE_PROGRESS);
            setContentView(R.layout.activity_web_view);

            String link = getIntent().getExtras().getString("Link");

            myWebView = findViewById(R.id.webView);
            swipe = findViewById(R.id.swipe);
            myDownloadListener = new MyDownloadListener(WebViewActivity.this, atvName);

            progressBar = findViewById(R.id.progress_bar);
            progressBar.setMax(100);
            progressBar.setProgress(1);

            if (DetectConnection.haveNetworkConnection(this)) {
//                Toast.makeText(getApplicationContext(), "Está conectado a internet", Toast.LENGTH_SHORT).show();
                WebSettings webSettings = myWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
//                webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
                webSettings.setDomStorageEnabled(true);
                webSettings.setAllowFileAccess(true);
                webSettings.setBuiltInZoomControls(true);
                webSettings.setDisplayZoomControls(false);
                webSettings.setAllowFileAccessFromFileURLs(true);
                //webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                Locale.setDefault(new Locale("pt", "BR"));

                myWebView.setWebChromeClient(new MyChromeClient(WebViewActivity.this, progressBar));
                myWebView.setWebViewClient(new MyWebViewClient(WebViewActivity.this, progressBar, swipe));
                myWebView.setDownloadListener(myDownloadListener);

                swipe.setRefreshing(true);

                //listener para atualizar a pagina quando deslizar a tela para baixo
                swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        myWebView.reload();
                    }
                });

                myWebView.loadUrl(link);
            } else {
                Toast.makeText(getApplicationContext(), "Sem internet", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error :" + e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        myWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        myWebView.restoreState(savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        Log.v("Permissao","code: "+requestCode);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("Permissao","Permission: "+permissions[0]+ " was "+grantResults[0]);
                    myDownloadListener.loadFile(myDownloadListener.atvName);
                }else{
                    Log.e("Permissao", "Permissao nao garantida");
                    Toast toast = Toast.makeText(WebViewActivity.this,"O app precisa de permissão de acesso ao armazenamento, para que as atividades possa ser executadas.",Toast.LENGTH_LONG);
                    toast.show();
                }
                break;
            default:

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web_view, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_back:
                onBackPressed();
                break;
            case R.id.menu_forward:
                onForwardPressed();
                break;
            case R.id.menu_reload:
                myWebView.reload();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onForwardPressed(){

        if(myWebView.canGoForward()){
            myWebView.goForward();
        }
    }

    @Override
    public void onBackPressed() {
        if(myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            confirmDialog(getApplicationContext());
        }
    }

    private void confirmDialog(Context context) {
        final AlertDialog alert = new AlertDialog.Builder(
                new ContextThemeWrapper(context, android.R.style.Theme_Dialog)).create();
        alert.setTitle("Alerta");
        alert.setMessage("Tem certeza que quer sair?");
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);

        alert.setButton(DialogInterface.BUTTON_POSITIVE, "Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
                finish();
            }
        });
        alert.setButton(DialogInterface.BUTTON_NEGATIVE, "Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
            }
        });

        alert.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // checar se o botao de voltar foi clicado e se tem paginas para voltar
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        // se nao tiver nenhuma pagina para voltar ele vai sair do main_fragment
        return super.onKeyDown(keyCode, event);
    }
}




