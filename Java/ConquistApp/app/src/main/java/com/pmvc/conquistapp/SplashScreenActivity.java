package com.pmvc.conquistapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.datami.smi.SmiSdk;
import com.datami.smi.SmiVpnSdk;
import com.datami.smi.internal.MessagingType;

import com.pmvc.conquistapp.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Handler handle = new Handler();
        setContentView(R.layout.activity_splash_screen);
        
        handle.postDelayed(new Runnable() {
            @Override public void run() {
                //abrirMain();
                abrirWeb();
            }
        }, 1000);
    }

    private void abrirMain() { //versao do app onde será utilizado api request em vez do login pelo site
        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void abrirWeb(){ //versao onde o app apenas executa a webview e tudo eh organizado pela plataforma online
        Intent intent = new Intent(SplashScreenActivity.this, WebViewActivity.class);
        intent.putExtra("Link", "http://smed.pmvc.ba.gov.br/estudoremoto/");
        SplashScreenActivity.this.startActivity(intent);
        finish();
    }
}
