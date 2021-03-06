package br.gov.ba.pmvc.vcapp;

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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Locale;

public class WebViewActivity extends AppCompatActivity {

    private MyWebView myWebView;
    private MyWebChromeClient webChromeClient;

    private SwipeRefreshLayout swipe;
//    private ProgressBar progressBar;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private MyDownloadListener myDownloadListener;

    @SuppressLint("SetJavaScriptEnable")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().requestFeature(Window.FEATURE_PROGRESS);
            setContentView(R.layout.activity_web_view);

            String link = getIntent().getExtras().getString("Link");

            myWebView = (MyWebView) findViewById(R.id.webView);

            // Initialize the VideoEnabledWebChromeClient and set event handlers
            View nonVideoLayout = findViewById(R.id.nonVideoLayout);
            ViewGroup videoLayout = (ViewGroup) findViewById(R.id.videoLayout);
            View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null);
            webChromeClient = new MyWebChromeClient(nonVideoLayout, videoLayout, loadingView, myWebView) {
                // Subscribe to standard events, such as onProgressChanged()...
                @Override
                public void onProgressChanged(WebView view, int progress) {
//                    progressBar.setProgress(progress);
                }
            };
            webChromeClient.setOnToggledFullscreen(new MyWebChromeClient.ToggledFullscreenCallback() {
                @Override
                public void toggledFullscreen(boolean fullscreen) {
                    // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                    if (fullscreen) {
                        WindowManager.LayoutParams attrs = getWindow().getAttributes();
                        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                        attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                        getWindow().setAttributes(attrs);
                        if (android.os.Build.VERSION.SDK_INT >= 14) {
                            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                        }
                    } else {
                        WindowManager.LayoutParams attrs = getWindow().getAttributes();
                        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                        attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                        getWindow().setAttributes(attrs);
                        if (android.os.Build.VERSION.SDK_INT >= 14) {
                            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                        }
                    }
                }
            });

            // ----------------------------------------------------------------------------- \\
            swipe = findViewById(R.id.swipe);
            myDownloadListener = new MyDownloadListener(WebViewActivity.this);

//            progressBar = findViewById(R.id.progress_bar);
//            progressBar.setMax(100);
//            progressBar.setProgress(1);

            if (DetectConnection.haveNetworkConnection(this)) {
                WebSettings webSettings = myWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setDomStorageEnabled(true);
                webSettings.setAllowFileAccess(true);
                webSettings.setBuiltInZoomControls(true);
                webSettings.setDisplayZoomControls(false);
                webSettings.setAllowFileAccessFromFileURLs(true);
//              webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                Locale.setDefault(new Locale("pt", "BR"));

                swipe.setRefreshing(true);

//                myWebView.setWebChromeClient(new MyChromeClient(WebViewActivity.this, progressBar));
                myWebView.setWebChromeClient(webChromeClient);
//                myWebView.setWebViewClient(new MyWebViewClient(WebViewActivity.this, progressBar, swipe));
                myWebView.setWebViewClient(new MyWebViewClient(WebViewActivity.this, swipe));
                myWebView.setDownloadListener(myDownloadListener);

                //listener para atualizar a pagina quando deslizar a tela para baixo
                swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        myWebView.reload();
                    }
                });

                myWebView.loadUrl(link);
            } else {
                ToastStack.createToast(getApplicationContext(), "Sem internet", Toast.LENGTH_SHORT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastStack.createToast(this, "Error :" + e, Toast.LENGTH_SHORT);
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v("Permissao", "code: " + requestCode);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("Permissao", "Permission: " + permissions[0] + " was " + grantResults[0]);
                    myDownloadListener.onPermissionGranted();
                } else {
                    Log.e("Permissao", "Permissao nao garantida");
                    ToastStack.createToast(WebViewActivity.this, "O app precisa de permissão de acesso ao armazenamento, para que as atividades possa ser executadas.", Toast.LENGTH_LONG);
                }
                break;
            default:
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web_view, menu);

        return super.onCreateOptionsMenu(menu);
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

    private void onForwardPressed() {
        if (myWebView.canGoForward()) {
            myWebView.goForward();
        }
    }

    @Override
    public void onBackPressed() {
        if (!webChromeClient.onBackPressed()) {
            if (myWebView.canGoBack()) {
                myWebView.goBack();
            } else {
                confirmDialog(getApplicationContext());
            }
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




