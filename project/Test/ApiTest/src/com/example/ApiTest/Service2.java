package com.example.ApiTest;

import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by zhangyong6 on 2015/3/2.
 */
public class Service2 extends BaseService {

    @Override
    String getTag() {
        return Service2.class.getSimpleName();
    }

    public Service2() {
        tag = getTag();
    }


    @Override
    public IBinder onBind(final Intent intent) {
        super.onBind(intent);
        return new Binder2.Stub() {

            private Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public int ping(int inValue) throws RemoteException {

                int i = inValue + 15;

                final String msg = String.format(">>服务%s:onBind,intent=%s ping=%s,value=%s", tag, intent, inValue, i);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Service2.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e(tag, msg);

                return i;
            }

            @Override
            public String pingStr(String inValue) throws RemoteException {
                String i = inValue + ",Yes,very much!";

                final String msg = String.format(">>服务%s:onBind,intent=%s ping=%s,value=%s", tag, intent, inValue, i);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Service2.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e(tag, msg);

                return i;
            }
        };
    }
}
