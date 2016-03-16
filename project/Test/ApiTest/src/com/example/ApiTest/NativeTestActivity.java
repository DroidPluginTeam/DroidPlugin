package com.example.ApiTest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.morgoo.nativec.NativeCHelper;

public class NativeTestActivity extends Activity {

    private static final String TAG = NativeTestActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_test);
        Log.e(TAG, " NativeCHelper.ping()=" + NativeCHelper.ping());
    }

}
