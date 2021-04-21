package com.pmvc.conquistapp;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDexApplication;

import com.datami.smi.*;
import com.datami.smi.internal.MessagingType;

public class MainApplication extends MultiDexApplication implements SdStateChangeListener {

    private Context context;
    private int duration;
    SdState tempState;

    private static final String TAG = WebViewActivity.class.getName();
    protected static SdState sdState;

    @Override
    public void onCreate() {
        super.onCreate();

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
        duration = Toast.LENGTH_SHORT;
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
        final String tempMSG = ""+text;
        ContextCompat.getMainExecutor(context).execute(()  -> {
                    Toast.makeText(context, tempMSG, duration).show();
        });
    }


}
