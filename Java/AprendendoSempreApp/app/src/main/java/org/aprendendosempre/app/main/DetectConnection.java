package org.aprendendosempre.app.main;

import android.content.Context;
import android.net.ConnectivityManager;

public class DetectConnection {
    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return (connectivityManager.getActiveNetworkInfo() != null)
                && connectivityManager.getActiveNetworkInfo().isAvailable();
    }
}
