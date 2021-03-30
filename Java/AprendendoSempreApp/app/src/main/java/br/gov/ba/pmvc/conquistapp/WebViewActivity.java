package br.gov.ba.pmvc.conquistapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.datami.smi.SdState;
import com.google.common.net.InternetDomainName;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class WebViewActivity extends AppCompatActivity {

    private WebView myWebView;
    private Exception exception;
    private SwipeRefreshLayout swipe;
    private ProgressBar progressBar;

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

                myWebView.setWebChromeClient(new MyChromeClient(WebViewActivity.this,progressBar,WebViewActivity.this));
                myWebView.setWebViewClient(new MyWebViewClient(WebViewActivity.this,progressBar,swipe));
                myWebView.setDownloadListener(new MyDownloadListener(WebViewActivity.this,WebViewActivity.this,atvName));

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
            exception = e;
            Toast.makeText(this, "Error :" + e , Toast.LENGTH_SHORT).show();
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



    ///ver se remove isso depois
    /*private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
//            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Log.d("Error", description);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            view.loadUrl(
                    "javascript:(function f() {" +
                    "var email = document.getElementsByName('identifier');" +
                    "email[0].oninput = function(value) {" +
                    "if(!/^\\w+([\\.-]?\\w+)*(@)?((e(d(u(c(a(d(o(r)?)?)?)?)?)?)?)?|(a(l(u(n(o)?)?)?)?))?(\\.)?(e(d(u(\\.(e(s(\\.(g(o(v(\\.(b(r)?)?)?)?)?)?)?)?)?)?)?)?)?$/.test(email[0].value)){" +
                    "email[0].value = '';" +
                    "email.parentNode.parentNode.parentNode.insertAdjacentHTML('afterend', 'Apenas domínio edu.es.gov.br!');" +
                    "return false;" +
                    "}" +
                    "}" +
                    "})()");
//            progressBar.setVisibility(View.INVISIBLE);
            myWebView.setVisibility(View.VISIBLE);
        }



        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            try {
                 if(url.startsWith("javascript"))
                     return false;

                 if (url.startsWith("http") || url.startsWith("https"))
                 {
                     if(MainApplication.sdState == SdState.SD_AVAILABLE)
                     {
                        URL urlEntrada = new URL(url);
                        List<String> urlsPermitidas = new ArrayList<String>(25);
                        urlsPermitidas.add("aprendendosempre.org");
                        urlsPermitidas.add("aprendizap.com.br");
                        urlsPermitidas.add("arvoreeducacao.com.br");
                        urlsPermitidas.add("avamec.mec.gov.br");
                        urlsPermitidas.add("escoladigital.org.br");
                        urlsPermitidas.add("google.com");
                        urlsPermitidas.add("khanacademy.org");
                        urlsPermitidas.add("kinedu.com");
                        urlsPermitidas.add("novaescola.org.br");
                        urlsPermitidas.add("googledrive.com");
                        urlsPermitidas.add("ssl.google-analytics.com");
                        urlsPermitidas.add("s.ytimg.com");
                        urlsPermitidas.add("googleapis.com");
                        urlsPermitidas.add("googleusercontent.com");
                        urlsPermitidas.add("gstatic.com");
                        urlsPermitidas.add("gvt1.com");
                        urlsPermitidas.add("forms.gle");
                        urlsPermitidas.add("whatsapp.com");
                        urlsPermitidas.add("bit.ly");

                        //TODO: fazer um filtro inteligente de URLs
                        for (int i = 0; i <= urlsPermitidas.size() -1; i++)
                        {

                            String urlAllowed = urlsPermitidas.get(i);
                            String host = urlEntrada.getHost();
                            InternetDomainName domain = InternetDomainName.from(host).topPrivateDomain();
                            String urlInput = domain.toString();
                            if(urlInput.equals(urlAllowed)){
                                webView.loadUrl(url);
                                return true;
                            }
                        }
                        Log.d("ControleAcesso", "Acesso negado a " + url);
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(getApplicationContext(), "Acesso negado.", duration);
                        toast.show();
                        return true;
                    }
                    else
                        return false;
                }

                if (url.startsWith("intent://")) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        PackageManager packageManager = webView.getContext().getPackageManager();
                        if (intent != null) {
                            webView.stopLoading();
                            ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
                            if (info != null) {
                                webView.getContext().startActivity(intent);
                            } else {
                                Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(
                                        Uri.parse("market://details?id=" + intent.getPackage()));
                                if (marketIntent.resolveActivity(packageManager) != null) {
                                    getApplicationContext().startActivity(marketIntent);
                                    return true;
                                }
                            }
                            return true;
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
            return true;
        }
    }*/
}




