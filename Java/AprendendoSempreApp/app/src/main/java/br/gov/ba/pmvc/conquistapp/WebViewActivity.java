package br.gov.ba.pmvc.conquistapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class WebViewActivity extends AppCompatActivity {

    private WebView myWebView;
    private SwipeRefreshLayout swipe;
    private ProgressBar progressBar;

    private String webViewTitle = "";
    private String url = "";

    private static final int PERMISSION_REQUEST_CODE = 1;
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
                webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                Locale.setDefault(new Locale("pt", "BR"));

                myWebView.setWebChromeClient(new MyChromeClient(WebViewActivity.this, progressBar, WebViewActivity.this));
                myWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        progressBar.setVisibility(View.VISIBLE);
                    }


                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        view.stopLoading();
                        Toast toast = Toast.makeText(WebViewActivity.this, "Sem conexão com a internet!", Toast.LENGTH_SHORT);
                        toast.show();
                        view.loadUrl("file:///android_asset/error.html");
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        progressBar.setVisibility(View.GONE);
                        swipe.setRefreshing(false);
                        if (view.getTitle() != null && view.getTitle().length() > 0) {
                            webViewTitle = view.getTitle();
                        }

//                        getSupportActionBar().setTitle(actionBarTitle);
                        getSupportActionBar().setSubtitle(webViewTitle);
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
                });
//                myWebView.setWebViewClient(new MyWebViewClient(WebViewActivity.this, progressBar, swipe));
                myWebView.setDownloadListener(new MyDownloadListener(WebViewActivity.this, WebViewActivity.this, atvName));

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
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("Permissao 3","Permission: "+permissions[0]+ "was "+grantResults[0]);

                    File sdcard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdcard.getAbsolutePath() + "/Download/" + atvName);
                    if (dir.exists()) {
                        Log.e("entrou no dir", "Dir do onRequestPermission");
                        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        Uri uri = FileProvider.getUriForFile(WebViewActivity.this,
                                WebViewActivity.this.getApplicationContext().getPackageName() + ".provider", dir);
                        pdfIntent.setDataAndType(uri, "application/pdf");

                        Intent chooser = Intent.createChooser(pdfIntent, "Abrir arquivo com");
//                        chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try {
                            startActivity(chooser);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(WebViewActivity.this, "Error :" + e , Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Log.e("Negacao", "Permission Denied, You cannot use local drive .");
                }
                break;
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




