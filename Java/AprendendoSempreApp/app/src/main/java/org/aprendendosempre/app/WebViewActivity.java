package org.aprendendosempre.app;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.datami.smi.SdState;
import com.google.common.net.InternetDomainName;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WebViewActivity extends AppCompatActivity {

    String url = "";

    private static final int WRITE_PERMISSION = 1001;

    private WebView myWebView;
    DownloadManager dm;
    String uriString;
    private long enq;
    Exception exception;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_web_view);
            myWebView = findViewById(R.id.webView);

            String link = getIntent().getExtras().getString("Link");

            progressBar = findViewById(R.id.progress);
            progressBar.setVisibility(View.VISIBLE);

            myWebView.setWebViewClient(new MyWebViewClient());
            WebSettings webSettings = myWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setAllowFileAccess(true);
            webSettings.setAllowFileAccessFromFileURLs(true);
            Locale.setDefault(new Locale("pt", "BR"));
            myWebView.loadUrl(link);

            myWebView.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    progressBar.setVisibility(View.GONE);
                    myWebView.setVisibility(View.VISIBLE);
                }
            });

            // funcao para abrir o arquivo apos fazer download
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                        long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                        DownloadManager.Query query = new DownloadManager.Query();
                        query.setFilterById(enq);
                        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        Cursor c = dm.query(query);
                        if (c.moveToFirst()) {
                            int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                            if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                                uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            }
                        }
                    }
                }
            };

            // funcao que habilita o download via download manager pelo webview
            myWebView.setDownloadListener(new DownloadListener() {

                public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                            String mimetype, long contentLength) {

                    Uri downloadUri = Uri.parse(url);
                    DownloadManager.Request request = new DownloadManager.Request(downloadUri);
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                            .setAllowedOverMetered(true)
                            .setAllowedOverRoaming(true);
//                            .allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Atividade.pdf");                    dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//                    enq = dm.enqueue(request);
                    dm.enqueue(request);
                    Toast.makeText(WebViewActivity.this, "Download iniciado", Toast.LENGTH_SHORT).show();
                    OpenPDF();
                }

                public void OpenPDF() {
                    File file = new File(Environment.getExternalStorageDirectory() +
                            "/Download/" + "Atividade.pdf");
                    if (!file.isDirectory()) {
                        file.mkdir();
                    }
                    Intent pdfIntent = new Intent("com.adobe.reader");
                    pdfIntent.setType("application/pdf");
                    pdfIntent.setAction(Intent.ACTION_VIEW);
                    pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    Uri uri = FileProvider.getUriForFile(WebViewActivity.this,
                            WebViewActivity.this.getApplicationContext().getPackageName() + ".provider", file);
                    pdfIntent.setDataAndType(uri, "application/pdf");

                    Intent intent = Intent.createChooser(pdfIntent, "Open File");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(WebViewActivity.this, "Error :" + e , Toast.LENGTH_SHORT).show();
                    }
                }

                public void OpenPDFExample() {
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator +
                            "myPDFfile.pdf");
                    //File file = new File(" content://storage/emulated/0/Download/myPDFfile.pdf");
                    Uri path = FileProvider.getUriForFile(WebViewActivity.this,WebViewActivity.this.getApplicationContext().getPackageName() + ".provider",file);
                    Log.i("Fragment2", String.valueOf(path));
                    Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                    pdfOpenintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    pdfOpenintent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    pdfOpenintent.setDataAndType(path, "application/pdf");
                    try {
                        startActivity(pdfOpenintent);
                    } catch (ActivityNotFoundException e) {
                        Log.d("",""+e);
                    }
                }

                public void OpenPDF2() {
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator +
                            "myPDFfile.pdf");
                    Intent target = new Intent(Intent.ACTION_VIEW);
                    target.setDataAndType(Uri.fromFile(file),"application/pdf");
                    target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                    Intent intent = Intent.createChooser(target, "Open File");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        // Instruct the user to install a PDF reader here, or something
                    }
                }
            });

//          registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        } catch (Exception e) {
            exception = e;
            Toast.makeText(this, "Error :" + e , Toast.LENGTH_SHORT).show();
        }
    }

//    // funcao para abrir o arquivo apos fazer download
//    BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
//                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
//                DownloadManager.Query query = new DownloadManager.Query();
//                query.setFilterById(enq);
//                dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//                Cursor c = dm.query(query);
//                if (c.moveToFirst()) {
//                    int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
//                    if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
//                        uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
//                    }
//                }
//            }
//        }
//    };
//
//    @Override
//    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
//        return super.registerReceiver(this.receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
//    }

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
    public void onBackPressed(){
        if(myWebView.canGoBack()){
            myWebView.goBack();
        }else{
            finish();
        }
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
            progressBar.setVisibility(View.VISIBLE);
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
            progressBar.setVisibility(View.INVISIBLE);
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


