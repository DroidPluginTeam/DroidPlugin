package com.example.ApiTest;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * Created by zhangyong6 on 2015/3/2.
 */
public class ContentProviderTest extends AppCompatActivity implements OnClickListener {
    protected static String TAG = "ContentProviderTest1";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_provider);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);
        TAG = "ContentProviderTest1";
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Uri sUri = getsUri();
        if (id == R.id.button1) {
            Cursor cursor = getContentResolver().query(sUri, new String[]{"name", "sex"}, "a=? and b=?", new String[]{"11", "22"}, "id acs");
            String msg = "查询失败";
            if (cursor != null) {
                msg = String.format("query结果：%s", DatabaseUtils.dumpCursorToString(cursor));
                cursor.close();
            }
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, msg);
        } else if (id == R.id.button2) {
            ContentValues value = new ContentValues();
            value.put("name", "哈哈");
            int re = getContentResolver().update(sUri, value, "a=? and b=?", new String[]{"11", "22"});
            String msg = String.format("update结果：%s", re);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, msg);
        } else if (id == R.id.button3) {
            int re = getContentResolver().delete(sUri, "a=? and b=?", new String[]{"11", "22"});
            String msg = String.format("delete结果：%s", re);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, msg);
        } else if (id == R.id.button4) {
            Bundle ex = new Bundle();
            ex.putString("name", "哈哈");
            Bundle re = getContentResolver().call(sUri, "method1", "arg1", ex);
            String msg = String.format("call结果：%s", re);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, msg);
        } else if (id == R.id.button5) {
            String re = getContentResolver().getType(sUri);
            String msg = String.format("getType结果：%s", re);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, msg);
        } else if (id == R.id.button6) {
            ContentValues value = new ContentValues();
            value.put("name", "哈哈");
            Uri re = getContentResolver().insert(sUri, value);
            String msg = String.format("insert结果：%s", re);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, msg);
        }
    }

    protected Uri getsUri() {
        return MyContentProvider1.sUri;
    }
}