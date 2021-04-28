package br.gov.ba.pmvc.vcapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;

public class ToastStack {

    static ArrayList<Toast> toastQueue = new ArrayList<>();
    static Toast lastState = null;

    public static void createStateToast(Context context,String message, int lenght){
        lastState = Toast.makeText(context,message,lenght);
        showToast();
    }

    public static void createToast(Context context,String message, int lenght){
        Toast temp = Toast.makeText(context,message,lenght);
        toastQueue.add(temp);
        showToast();
    }

    public static void showToast(){
        if(MainApplication.isBackground){
            return;
        }
        if(lastState != null){
            lastState.show();
            lastState = null;
        }
        while (toastQueue.size() > 0){
            toastQueue.get(0).show();
            toastQueue.remove(0);
        }
    }
}
