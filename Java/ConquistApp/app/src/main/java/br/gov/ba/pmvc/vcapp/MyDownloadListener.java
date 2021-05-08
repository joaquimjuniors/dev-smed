package br.gov.ba.pmvc.vcapp;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.DownloadBlock;
import com.tonyodev.fetch2core.Extras;
import com.tonyodev.fetch2core.FetchObserver;
import com.tonyodev.fetch2core.Func;
import com.tonyodev.fetch2core.MutableExtras;
import com.tonyodev.fetch2core.Reason;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

import static androidx.core.app.ActivityCompat.requestPermissions;

public class MyDownloadListener implements DownloadListener, FetchObserver<Download> {

    Activity activity;
    Context context;

    private String atvName;
    private String atvPath;
    private Request request;
    private Fetch fetch;
    private FetchListener fetchListener;
    private String gUrl;

    public MyDownloadListener(Activity act) {
        super();
        activity = act;
        context = activity.getApplicationContext();
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                String mimetype, long contentLength) {

        atvName = URLUtil.guessFileName(url, contentDisposition, mimetype);
        atvPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + atvName;
        gUrl = url;

        fetch = Fetch.Impl.getDefaultInstance();
        createFetchListener();
        if(checkReadWritePermission()){
            onPermissionGranted();
        }//caso nao tenha permissao o onPermissionGranted vai ser executado pelo onRequestPermissionsResult no WevViewActivity
    }

    public void onPermissionGranted(){
        File dir = new File(atvPath);
        if (!dir.exists()) { //verifica se o arquivo existe antes de iniciar o pedir acesso e iniciar o download
            enqueueDownload(gUrl);
        } else {
            loadFile(atvName);
        }
    }

    private void enqueueDownload(String url) {
        request = new Request(url, atvPath);
        request.setExtras(getExtrasForRequest(request));

        fetch.attachFetchObserversForDownload(request.getId(), this)
                .enqueue(request, new Func<Request>() {
                    @Override
                    public void call(@NotNull Request result) {
                        request = result;
                        ToastStack.createToast(context, "Download iniciado", Toast.LENGTH_SHORT);
                    }
                }, new Func<Error>() {
                    @Override
                    public void call(@NotNull Error result) {
                        Log.e("Erro", result.toString());
                        ToastStack.createToast(context, result.toString(), Toast.LENGTH_SHORT);
                    }
                });
    }



    public void loadFile(String atividadeNome) {
        fetch.removeListener(fetchListener);
        if (Build.VERSION.SDK_INT >= 23) {
            Log.e("SDK >= 23", "versao maior que 23");
            File dir = new File(atvPath);
            openPDF(dir);
        } else {
            //por enquando ele esta abrindo, mas caso for fazer algo diferente na webview para celulares antigos, eh aqui
            System.out.println("Teste");
            Log.e("SDK < 23", "versao menor que 23");
            File dir = new File(atvPath);
            openPDF(dir);
        }
    }

    // funcao para abrir o pdf
    public void openPDF(File atv) {
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", atv);
        pdfIntent.setDataAndType(uri, "application/pdf");

        Intent chooser = Intent.createChooser(pdfIntent, "Abrir arquivo com:");
        chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(chooser); //se for usar o intent chooser
        } catch (ActivityNotFoundException e) {
            ToastStack.createToast(context, "Error :" + e, Toast.LENGTH_SHORT);
        }
    }

    private boolean checkReadWritePermission() {
        if(ContextCompat.checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
            return true;
        }else{
            requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},1);
            return false;
        }
    }

    private Extras getExtrasForRequest(Request request) {
        final MutableExtras extras = new MutableExtras();
        extras.putBoolean("testBoolean", true);
        extras.putString("testString", "test");
        extras.putFloat("testFloat", Float.MIN_VALUE);
        extras.putDouble("testDouble",Double.MIN_VALUE);
        extras.putInt("testInt", Integer.MAX_VALUE);
        extras.putLong("testLong", Long.MAX_VALUE);
        return extras;
    }

    @Override
    public void onChanged(Download download, @NotNull Reason reason) { }

    public void createFetchListener(){
        fetchListener = new FetchListener() {
            @Override
            public void onWaitingNetwork(@NotNull Download download) {}
            @Override
            public void onStarted(@NotNull Download download, @NotNull List<? extends DownloadBlock> list, int i) {}
            @Override
            public void onError(@NotNull Download download, @NotNull Error error, @Nullable Throwable throwable) {}
            @Override
            public void onDownloadBlockUpdated(@NotNull Download download, @NotNull DownloadBlock downloadBlock, int i) {}
            @Override
            public void onAdded(@NotNull Download download) {}
            @Override
            public void onQueued(@NotNull Download download, boolean waitingOnNetwork) {}
            @Override
            public void onCompleted(@NotNull Download download) {
                try {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "0")
                            .setSmallIcon(R.drawable.ic_newlogo_light)
                            .setContentTitle("ConquistApp")
                            .setContentText("Download Finalizado!");
                    mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                    NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, mBuilder.build());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                loadFile(atvName);
            }
            @Override
            public void onProgress(@NotNull Download download, long etaInMilliSeconds, long downloadedBytesPerSecond) {}
            @Override
            public void onPaused(@NotNull Download download) {}
            @Override
            public void onResumed(@NotNull Download download) {}
            @Override
            public void onCancelled(@NotNull Download download) {}
            @Override
            public void onRemoved(@NotNull Download download) {}
            @Override
            public void onDeleted(@NotNull Download download) {}
        };

        fetch.addListener(fetchListener);
    }
}
