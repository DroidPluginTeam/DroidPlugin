package com.example.ApiTest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by zhangyong6 on 2015/3/11.
 */
public abstract class BaseService extends Service {
    protected String tag = "haha";

    abstract String getTag();

    @Override
    public IBinder onBind(Intent intent) {
        String msg = String.format(">>服务%s:onBind,intent=%s", tag, intent);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.e(tag, msg);
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        String msg = String.format(">>服务%s:onUnbind,intent=%s", tag, intent);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.e(tag, msg);
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String msg = String.format(">>服务%s:onCreate", tag);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.e(tag, msg);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        String msg = String.format(">>服务%s:onStart,intent=%s,startId=%s", tag, intent, startId);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.e(tag, msg);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String msg = String.format(">>服务%s:onStartCommand,intent=%s,flags=%s,startId=%s", tag, intent, flags, startId);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.e(tag, msg);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        String msg = String.format(">>服务%s:onDestroy", tag);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.e(tag, msg);
        super.onDestroy();
    }

    @Override
    public void onRebind(Intent intent) {
        String msg = String.format(">>服务%s:onRebind,intent=%s", tag, intent);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.e(tag, msg);
        super.onRebind(intent);
    }
}
