package com.pmvc.conquistapp;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

public class MyDownloadListener implements DownloadListener {

    Activity activity;
    String atvName;
    Context context;

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
        DownloadManager dm;

        Uri downloadUri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverMetered(true);
        request.setAllowedOverRoaming(true);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, atvName);

        dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        request.setMimeType("application/pdf");
//
//                        if (checkPermission()) {
//                            Log.e("Certo", "Tudo ok");
//                        } else {
//                            Log.e("Primeiro log", "Nao tem permissao ainda");
//                            requestPermission();
//                        }

        File sdcard = Environment.getExternalStorageDirectory();
        File dir = new File(sdcard.getAbsolutePath() + "/Download/" + atvName);

        if (!dir.exists()) {
            dm.enqueue(request);
            Toast.makeText(context, "Download iniciado", Toast.LENGTH_SHORT).show();
            context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        } else {
            loadFile(atvName);
        }
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() { //receiver que executa o arquivo quando o download e' finalizado
        public void onReceive(Context ctxt, Intent intent) {
            loadFile(atvName);
        }
    };

    public void loadFile(String atividadeNome) {
        if (Build.VERSION.SDK_INT >= 23) {
            Log.e("SDK >= 23", "versao maior que 23");
            if (checkPermission()) {
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
            Toast.makeText(context, "Error :" + e, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            Log.v("Permissao 1","Permission is granted");
            return true;
        } else {
            Log.v("Negacao 1","Permission is revoked");
            return false;
        }
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


}
