package com.example.ApiTest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * Created by zhangyong6 on 2015/3/2.
 */
public class BroadcastReceiverTest extends AppCompatActivity implements OnClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.broadcast_receiver);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
    }

    private static class MyBroadcastReceiver extends BroadcastReceiver {
        public static final String ACTION = "com.example.ApiTest.MyBroadcastReceiver";
        private static final String TAG = "MyBroadcastReceiver";

        public static void send(Context c) {
            Intent intent = new Intent(ACTION);
            c.sendBroadcast(intent);
        }

        public static void reg(Context c, MyBroadcastReceiver re) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION);
            c.registerReceiver(re, filter);
        }

        public static void unreg(Context c, MyBroadcastReceiver re) {
            c.unregisterReceiver(re);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = String.format(">>>>%s onReceive:context=%s,intent=%s", TAG, context, intent);
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, msg);
        }
    }

    private MyBroadcastReceiver re = new MyBroadcastReceiver();

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button1) {
            MyBroadcastReceiver.reg(this, re);
        } else if (id == R.id.button2) {
            MyBroadcastReceiver.unreg(this, re);
        } else if (id == R.id.button3) {
            MyBroadcastReceiver.send(this);
        } else if (id == R.id.button4) {
            StaticBroadcastReceiver.send(this);
        }
    }
}