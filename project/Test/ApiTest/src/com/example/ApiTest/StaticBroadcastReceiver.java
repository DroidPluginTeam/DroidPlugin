package com.example.ApiTest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by zhangyong6 on 2015/3/3.
 */
public class StaticBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION = "com.example.ApiTest.StaticBroadcastReceiver";
    private static final String TAG = "StaticBroadcastReceiver";

    public static void send(Context c) {
        Intent intent = new Intent(ACTION);
        c.sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = String.format(">>>>%s onReceive:context=%s,intent=%s", TAG, context, intent);
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, msg);
    }
}
