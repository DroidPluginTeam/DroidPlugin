package com.example.ApiTest;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * Created by zhangyong6 on 2015/3/3.
 */
public class NotificationTest extends AppCompatActivity implements OnClickListener {
    private static final String TAG = NotificationTest.class.getSimpleName();
    private static final int ID = 0x1986;
    private static final int ID2 = 0x1988;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_test);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        NotificationManagerCompat nm = NotificationManagerCompat.from(this);
        int id = v.getId();
        if (id == R.id.button1) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.drawable.ic_launcher);
            builder.setAutoCancel(true);
            builder.setContentInfo("ContentInfo1").
                    setContentText("ContentText1").
                    setContentTitle("ContentTitle1")
                    .setTicker("Ticker1");
            PendingIntent pd = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(this, MyActivity.class), 0);
            builder.setContentIntent(pd);
            builder.setWhen(System.currentTimeMillis());
            nm.notify(ID, builder.build());
            String msg = "发送通知1成功";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, msg);
        } else if (id == R.id.button2) {
            nm.cancel(ID);
            String msg = "取消通知1成功";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, msg);
        } else if (id == R.id.button3) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.drawable.ic_launcher);
            builder.setAutoCancel(true);
            builder.setContentInfo("ContentInfo2").
                    setContentText("ContentText2").
                    setContentTitle("ContentTitle2")
                    .setTicker("Ticker2");
            PendingIntent pd = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(this, MyActivity.class), 0);
            builder.setContentIntent(pd);
            builder.setWhen(System.currentTimeMillis());
            nm.notify("tag1", ID2, builder.build());
            String msg = "发送通知2成功";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, msg);
        } else if (id == R.id.button4) {

            nm.cancel("tag1", ID2);
            String msg = "取消通知2成功";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, msg);
        }
    }
}