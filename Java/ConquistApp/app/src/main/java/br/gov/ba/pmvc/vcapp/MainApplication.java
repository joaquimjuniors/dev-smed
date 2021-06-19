package br.gov.ba.pmvc.vcapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDexApplication;

import com.datami.smi.SdState;
import com.datami.smi.SdStateChangeListener;
import com.datami.smi.SmiResult;
import com.datami.smi.SmiVpnSdk;
import com.datami.smi.internal.MessagingType;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.HttpUrlConnectionDownloader;
import com.tonyodev.fetch2core.Downloader;

public class MainApplication extends MultiDexApplication implements SdStateChangeListener, Application.ActivityLifecycleCallbacks {

    private Context context;
    private int duration;
    SdState tempState;
    public static boolean isBackground;

    private static final String TAG = WebViewActivity.class.getName();
    protected static SdState sdState;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        try {
            String mySdkKey = getString(R.string.SDK_KEY);
            int sdIconId = R.drawable.ic_launcher_foreground;
            //List<String> exclusionDomains = new ArrayList<String>(2);
            //exclusionDomains.add("www.google.com");
            //exclusionDomains.add("www.google.com.br");
            //SmiSdk.initSponsoredData(mySdkKey, this, "", sdIconId, false);
            SmiVpnSdk.initSponsoredData(mySdkKey, this, sdIconId, MessagingType.BOTH);
        }catch(Exception e){
            e.printStackTrace();
        }

        context = getApplicationContext();
        duration = Toast.LENGTH_LONG;

        final FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(this)
                .enableLogging(true)
                .enableRetryOnNetworkGain(true)
                .setDownloadConcurrentLimit(3)
                .setHttpDownloader(new HttpUrlConnectionDownloader(Downloader.FileDownloaderType.PARALLEL))
                // OR
                //.setHttpDownloader(getOkHttpDownloader())
                .build();
        Fetch.Impl.setDefaultInstanceConfiguration(fetchConfiguration);
    }

    @Override
    public void onChange(SmiResult currentSmiResult) {
        sdState = currentSmiResult.getSdState();
        Log.d(TAG, "sponsored data state : "+sdState);
        CharSequence text = "";

        if(sdState == SdState.SD_AVAILABLE) {
            text = "Acesso via Dados Móveis. Seu acesso a esse site é gratuito.";
        } else if(sdState == SdState.SD_NOT_AVAILABLE) {
            text = "Acesso via Dados Móveis. Seu acesso a esse site poderá acarretar cobranças em seu plano de dados.";
            Log.d(TAG, " - reason: " + currentSmiResult.getSdReason());
        } else if(sdState == SdState.WIFI) {
            // device is in wifi
            text = "Acesso via wifi.";
            Log.d(TAG, "wifi - reason: " + currentSmiResult.getSdReason());
        }
        tempState = sdState;
        final String tempMSG = "" + text;
        ContextCompat.getMainExecutor(context).execute(()  -> {
            ToastStack.createStateToast(context,tempMSG,duration);
        });
    }


    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        isBackground = false;
        ToastStack.showToast();
        Log.e("StateACT","fore");
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        isBackground = true;
        Log.e("StateACT","back");
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
