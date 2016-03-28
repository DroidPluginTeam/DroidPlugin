package com.example.ApiTest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.morgoo.nativec.NativeCHelper;

public class NativeTestActivity extends AppCompatActivity {

    private static final String TAG = NativeTestActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_test);
        Log.e(TAG, " NativeCHelper.ping()=" + NativeCHelper.ping());
    }

}
