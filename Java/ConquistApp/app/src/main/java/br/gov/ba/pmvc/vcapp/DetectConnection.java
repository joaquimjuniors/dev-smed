package br.gov.ba.pmvc.vcapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class DetectConnection {

    public static boolean haveNetworkConnection(Context context) {
        boolean haveConnecetedWifi = false;
        boolean haveConnecetedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();

        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
                if (ni.isConnected()) {
                    haveConnecetedWifi = true;
                }
            }
            if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (ni.isConnected()) {
                    haveConnecetedMobile = true;
                }
            }
        }
        return haveConnecetedWifi || haveConnecetedMobile;
    }
}
