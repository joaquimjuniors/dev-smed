package br.gov.ba.pmvc.vcapp;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
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

import com.datami.smi.SmiResult;
import com.datami.smi.SmiSdk;
import com.datami.smi.SmiVpnSdk;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
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
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;
import static android.content.Context.DOWNLOAD_SERVICE;

public class MyDownloadListener implements DownloadListener, FetchObserver<Download> {

    Activity activity;
    String atvName;
    Context context;
    private Request request;
    private Fetch fetch;
    private FetchListener fetchListener;

    public MyDownloadListener(Activity act, String atv) {
        super();
        activity = act;
        atvName = atv;
        context = activity.getApplicationContext();
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                String mimetype, long contentLength) {

        atvName = URLUtil.guessFileName(url, contentDisposition, mimetype);
        fetch = Fetch.Impl.getDefaultInstance();
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
                }catch (Exception e){
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

        File sdcard = Environment.getExternalStorageDirectory();
        File dir = new File(sdcard.getAbsolutePath() + "/Download/" + atvName);

        if (!dir.exists()) {
            if(!checkWritePermission()){
                requestPermission();
            }
            enqueueDownload(url);
        } else {
            loadFile(atvName);
        }

    }
    private void enqueueDownload(String url) {
        File sdcard = Environment.getExternalStorageDirectory();
        final String filePath = sdcard.getAbsolutePath() + "/Download/" + atvName;
        request = new Request(url, filePath);
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
                        Log.d("SingleD Error: %1$s", result.toString());
                        ToastStack.createToast(context, "Erro ao fazer o download!", Toast.LENGTH_SHORT);
                    }
                });
    }



    public void loadFile(String atividadeNome) {
        fetch.removeListener(fetchListener);
        if (Build.VERSION.SDK_INT >= 23) {
            Log.e("SDK >= 23", "versao maior que 23");
            if (checkReadPermission()) {
                File sdcard = Environment.getExternalStorageDirectory();
                File dir = new File(sdcard.getAbsolutePath() + "/Download/" + atividadeNome);
                if (dir.exists()) {
                    Log.e("abrir", "vai tentar abrir");
                    openPDF(dir);
                }
            } else {
                requestPermission();
            }
        } else {
            //por enquando ele esta abrindo, mas caso for fazer algo diferente na webview para celulares antigos, e' aqui
            Log.e("SDK < 23", "versao menor que 23");
            if (checkReadPermission()) {
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

    public boolean checkReadPermission() {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            Log.v("Permissao 1","Permission is granted");
            return true;
        } else {
            Log.v("Negacao 1","Permission is revoked");
            return false;
        }
    }
    private boolean checkWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            return true;
        }
        return false;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            //se for ter alguma tela mostrando porque precisa da permissao, chama ela aqui
        } else {
            Log.e("Permissao", "Primeira vez ");
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
    public void onChanged(Download download, @NotNull Reason reason) {

    }
}
