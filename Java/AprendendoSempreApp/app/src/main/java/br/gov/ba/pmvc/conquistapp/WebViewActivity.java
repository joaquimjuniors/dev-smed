package br.gov.ba.pmvc.conquistapp;

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
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

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

    DownloadManager dm;
    Exception exception;
    SwipeRefreshLayout swipe;
    ProgressBar progressBar;
//    ProgressDialog progressDialog;

    private static final int PERMISSION_REQUEST_CODE = 100;
    String atvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            getWindow().requestFeature(Window.FEATURE_PROGRESS);
            setContentView(R.layout.activity_web_view);
            myWebView = findViewById(R.id.webView);

            String link = getIntent().getExtras().getString("Link");

            progressBar = findViewById(R.id.progress_bar);
            progressBar.setMax(100);
            progressBar.setProgress(1);

            swipe = findViewById(R.id.swipe);

            if (DetectConnection.haveNetworkConnection(this)) {
//                Toast.makeText(getApplicationContext(), "Está conectado a internet", Toast.LENGTH_SHORT).show();
                WebSettings webSettings = myWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
                webSettings.setDomStorageEnabled(true);
                webSettings.setAllowFileAccess(true);
                webSettings.setBuiltInZoomControls(true);
                webSettings.setDisplayZoomControls(false);
                webSettings.setAllowFileAccessFromFileURLs(true);
                webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                Locale.setDefault(new Locale("pt", "BR"));
                swipe.setRefreshing(true);

                //listener para atualizar a pagina quando deslizar a tela para baixo
                swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        myWebView.reload();
                    }
                });

                //listener para receber o progresso de carregamento da pagina e atualizar a barra de loading
                myWebView.setWebChromeClient(new WebChromeClient() {
                    public void onProgressChanged(WebView view, int progress) {
                        progressBar.setProgress(progress);
                        if (progress == 100) {
                            progressBar.setVisibility(View.GONE);

                        } else {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }
                });

                // solicitar a barra de progresso para a activity
                myWebView.setWebViewClient(new WebViewClient() {

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        myWebView.stopLoading();
                        Toast toast = Toast.makeText(WebViewActivity.this, "Sem conexão com a internet!", Toast.LENGTH_SHORT);
                        toast.show();
                        //myWebView.loadUrl("file:///android_asset/error.html");
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        progressBar.setVisibility(View.GONE);
                        swipe.setRefreshing(false);
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

                // funcao que habilita o download via download manager pelo webview
                myWebView.setDownloadListener(new DownloadListener() {

                    public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                                String mimetype, long contentLength) {
                        atvName = URLUtil.guessFileName(url, contentDisposition, mimetype);

                        Uri downloadUri = Uri.parse(url);
                        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                        }
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                        request.setAllowedOverMetered(true);
                        request.setAllowedOverRoaming(true);
                        request.allowScanningByMediaScanner();
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, atvName);

                        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        request.setMimeType("application/pdf");

                        File sdcard = Environment.getExternalStorageDirectory();
                        File dir = new File(sdcard.getAbsolutePath() + "/Download/" + atvName);
                        if (!dir.exists()) {
                            dm.enqueue(request);
                            Toast.makeText(WebViewActivity.this, "Download iniciado", Toast.LENGTH_SHORT).show();
                            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                        } else {
                            loadFile(atvName);
                        }
                    }

                    BroadcastReceiver onComplete = new BroadcastReceiver() {
                        public void onReceive(Context ctxt, Intent intent) {
                            loadFile(atvName);
                        }
                    };

                    public void loadFile(String atividadeNome) {
                        String state = Environment.getExternalStorageState();
                        if (Environment.MEDIA_MOUNTED.equals(state)) {
                            if (Build.VERSION.SDK_INT >= 23) {
                                Log.e("v1", "versao maior que 23");
                                if (checkPermission()) {
                                    File sdcard = Environment.getExternalStorageDirectory();
                                    File dir = new File(sdcard.getAbsolutePath() + "/Download/" + atividadeNome);
                                    if (dir.exists()) {
                                        Log.e("abrir", "vai tentar abrir");
                                        openPDF(dir);
                                    }
                                } else {
                                    requestPermission(); // Code for permission
                                }
                            } else {
                                //por enquando ele esta abrindo, mas caso for fazer algo diferente na webview para celulares antigos, e' aqui
                                Log.e("v1", "versao menor que 23");
                                if (checkPermission()) {
                                    File sdcard = Environment.getExternalStorageDirectory();
                                    File dir = new File(sdcard.getAbsolutePath() + "/Download/" + atividadeNome);
                                    if (dir.exists()) {
                                        openPDF(dir);
                                    }
                                } else {
                                    requestPermission(); // Code for permission
                                }
                            }
                        }
                    }

                    // funcao para abrir o pdf
                    public void openPDF(File atv) {
                        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
//                        pdfIntent.setAction(Intent.ACTION_VIEW);
                        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Uri uri = FileProvider.getUriForFile(WebViewActivity.this,
                                WebViewActivity.this.getApplicationContext().getPackageName() + ".provider", atv);
                        pdfIntent.setDataAndType(uri, "application/pdf");

                        Intent chooser = Intent.createChooser(pdfIntent, "Abrir arquivo com:");
                        chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try {
                            startActivity(chooser); //se for usar o intent chooser
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(WebViewActivity.this, "Error :" + e, Toast.LENGTH_SHORT).show();
                        }
                    }


                    //funcoes responsaveis por solicitar acesso a leitura para abrir o pdf
                    private boolean checkPermission() {
                        int result = ContextCompat.checkSelfPermission(WebViewActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (result == PackageManager.PERMISSION_GRANTED) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                    private void requestPermission() {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(WebViewActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            Toast.makeText(WebViewActivity.this,
                                    "Write External Storage permission allows us to read files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
                        } else {
                            Log.e("Request", "Foi no else do request");
                            ActivityCompat.requestPermissions(WebViewActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                        }
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

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            Log.d("Erro", "Não está conetado a internet");
        }
        return false;
    }

    // ICMP
    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("sucesso", "Permissao garantida, agora voce pode usar o drive local.");

                    File sdcard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdcard.getAbsolutePath() + "/Download/" + atvName);
                    if (dir.exists()) {
                        Log.e("entrou no dir", "Dir do requeste permissions");
                        // onde estava o erro
//                        File file = new File(dir, "" + atvName);

                        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        Uri uri = FileProvider.getUriForFile(WebViewActivity.this,
                                WebViewActivity.this.getApplicationContext().getPackageName() + ".provider", dir);
                        pdfIntent.setDataAndType(uri, "application/pdf");

                        Intent chooser = Intent.createChooser(pdfIntent, "Abrir arquivo com");
                        chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try {
                            startActivity(chooser);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(WebViewActivity.this, "Error :" + e , Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
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
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the main_fragment)
        return super.onKeyDown(keyCode, event);
    }

    private class MyWebViewClient extends WebViewClient {

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
    }
}


