package br.gov.ba.pmvc.conquistapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.datami.smi.SmiSdk;

import br.gov.ba.pmvc.conquistapp.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override public void run() {
                //abrirMain();
                abrirWeb();
            }
        }, 1000);
    }

    private void abrirMain() { //versao do app onde será utilizado api request em vez do login pelo site
        setContentView(R.layout.activity_splash_screen);

        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void abrirWeb(){ //versao onde o app apenas executa a webview e tudo eh organizado pela plataforma online
        try {
            String mySdkKey = getString(R.string.SDK_KEY);
            String myUserId = "";
            int sdIconId = R.drawable.ic_launcher_foreground;

//          List<String> exclusionDomains = new ArrayList<String>(2);
//          exclusionDomains.add("www.google.com");
//          exclusionDomains.add("www.google.com.br");
//          SmiSdk.initSponsoredData(mySdkKey, this, myUserId, sdIconId, false, exclusionDomains);
            SmiSdk.initSponsoredData(mySdkKey, this, myUserId, sdIconId, false);

            setContentView(R.layout.activity_splash_screen);

        } catch (Exception e) {

            Toast.makeText(this, "Error :" + e , Toast.LENGTH_LONG).show();
        }


        Intent intent = new Intent(SplashScreenActivity.this, WebViewActivity.class);
//        intent.putExtra("Link", "http://www.google.com/");
        intent.putExtra("Link", "http://smed.pmvc.ba.gov.br/estudoremoto/login-control/");
        SplashScreenActivity.this.startActivity(intent);
        finish();
    }
}
