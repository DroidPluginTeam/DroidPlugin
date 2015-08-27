package com.example.ApiTest;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * Created by zhangyong6 on 2015/3/2.
 */
public class ServiceTest1 extends Activity implements OnClickListener {


    private static final String TAG = "ServiceTest";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);
        findViewById(R.id.button7).setOnClickListener(this);
        findViewById(R.id.button8).setOnClickListener(this);
        service1 = new Intent(this, Service1.class);
        service2 = new Intent(this, Service2.class);
    }

    protected Intent service1, service2;

    private ServiceConnection sc1 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            String msg = String.format("服务%s,onServiceConnected：name=%s,service=%s", "service1", name, service);
            Toast.makeText(ServiceTest1.this, msg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, msg);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            String msg = String.format("服务%s,onServiceDisconnected：name=%s", "service1", name);
            Toast.makeText(ServiceTest1.this, msg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, msg);
        }
    };

    private ServiceConnection sc2 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            String msg = String.format("服务%s,onServiceConnected：name=%s,service=%s", "service2", name, service);
            Toast.makeText(ServiceTest1.this, msg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, msg);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            String msg = String.format("服务%s,onServiceDisconnected：name=%s", "service2", name);
            Toast.makeText(ServiceTest1.this, msg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, msg);
        }
    };

    @Override
    public void onClick(View v) {
        Context context = getApplicationContext();
        int id = v.getId();
        if (id == R.id.button1) {
            boolean re = context.bindService(service1, sc1, Context.BIND_AUTO_CREATE);
            String msg = String.format("绑定服务%s,结果：%s", "service1", re);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, msg);
        } else if (id == R.id.button2) {
            context.unbindService(sc1);
            String msg = String.format("解绑服务%s", "service1");
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, msg);
        } else if (id == R.id.button3) {
            boolean re = context.bindService(service2, sc2, Context.BIND_AUTO_CREATE);
            String msg = String.format("绑定服务%s,结果：%s", "service2", re);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, msg);
        } else if (id == R.id.button4) {
            context.unbindService(sc2);
            String msg = String.format("解绑服务%s", "service2");
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, msg);
        } else if (id == R.id.button5) {
            ComponentName cn = context.startService(service1);
            String msg = String.format("启动服务%s,结果：%s", "service1", cn);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, msg);
        } else if (id == R.id.button6) {
            boolean re = context.stopService(service1);
            String msg = String.format("停止服务%s,结果：%s", "service1", re);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, msg);
        } else if (id == R.id.button7) {
            ComponentName cn = context.startService(service2);
            String msg = String.format("启动服务%s,结果：%s", "service2", cn);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, msg);
        } else if (id == R.id.button8) {
            boolean re = context.stopService(service2);
            String msg = String.format("停止服务%s,结果：%s", "service2", re);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, msg);
        }
    }
}