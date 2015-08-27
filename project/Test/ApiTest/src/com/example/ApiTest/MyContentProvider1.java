package com.example.ApiTest;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;

/**
 * Created by zhangyong6 on 2015/3/3.
 */
public class MyContentProvider1 extends ContentProvider {

    public static final String name = "com.example.ApiTest.MyContentProvider1";
    public static final Uri sUri = Uri.parse("content://" + name);
    private static final String TAG = MyContentProvider1.class.getSimpleName();

    @Override
    public boolean onCreate() {
        String msg = String.format(">>>>%s onCreate", TAG);

        showMsg(msg);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String msg = String.format(">>>>%s query:uri=%s, projection=%s, selection=%s,selectionArgs=%s, sortOrder=%s", TAG, uri, projection, selection, Arrays.toString(selectionArgs), sortOrder);
        showMsg(msg);
        MatrixCursor c = new MatrixCursor(new String[]{"name", "sex"});
        c.addRow(new String[]{"张三", "男"});
        c.addRow(new String[]{"李四", "女"});
        c.addRow(new String[]{"王武", "不男不女"});
        return c;
    }

    private void showMsg(final String msg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
        Log.e(TAG, msg);
    }

    @Override
    public String getType(Uri uri) {
        String msg = String.format(">>>>%s getType:uri=%s", TAG, uri);
        showMsg(msg);
        return "text/x-zhangyong";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String msg = String.format(">>>>%s insert:uri=%s, values=%s", TAG, uri, values);
        showMsg(msg);
        return Uri.parse("content://" + name + "/12");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String msg = String.format(">>>>%s delete:uri=%s, selection=%s,  selectionArgs=%s", TAG, uri, selection, Arrays.toString(selectionArgs));
        showMsg(msg);
        return 66;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String msg = String.format(">>>>%s update:uri=%s, values=%s, selection=%s,  selectionArgs=%s", TAG, uri, values, selection, Arrays.toString(selectionArgs));
        showMsg(msg);
        return 77;
    }
}
