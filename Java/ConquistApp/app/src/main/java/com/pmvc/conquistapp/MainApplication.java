package com.pmvc.conquistapp;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.multidex.MultiDexApplication;

import com.datami.smi.*;

public class MainApplication extends MultiDexApplication implements SdStateChangeListener {

    private Toast toast;
    private Context context;
    private int duration;
    SdState tempState;

    private static final String TAG = WebViewActivity.class.getName();
    protected static SdState sdState;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        duration = Toast.LENGTH_SHORT;
    }

    @Override
    public void onChange(SmiResult currentSmiResult) {
        sdState = currentSmiResult.getSdState();
        Log.d(TAG, "sponsored data state : "+sdState);
        CharSequence text = "";
        if(sdState == SdState.SD_AVAILABLE && tempState != SdState.SD_AVAILABLE) {
            text = "Seu acesso a esse site é gratuito.";
        } else if(sdState == SdState.SD_NOT_AVAILABLE && tempState != SdState.SD_NOT_AVAILABLE) {
            text = "Seu acesso a esse site poderá acarretar cobranças em seu plano de dados.";
            Log.d(TAG, " - reason: " + currentSmiResult.getSdReason());
        } else if(sdState == SdState.WIFI && tempState != SdState.WIFI) {
            // device is in wifi
            text = "Acesso via wifi.";
            Log.d(TAG, "wifi - reason: " + currentSmiResult.getSdReason());
        }
        tempState = sdState;
        toast = Toast.makeText(context, text, duration);
        toast.show();
        Log.e("stateChange",""+text);
    }


}
