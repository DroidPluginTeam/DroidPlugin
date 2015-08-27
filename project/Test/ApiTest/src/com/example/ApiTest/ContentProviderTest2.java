package com.example.ApiTest;

import android.net.Uri;
import android.os.Bundle;

/**
 * Created by zhangyong6 on 2015/3/11.
 */
public class ContentProviderTest2 extends ContentProviderTest {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = "ContentProviderTest2";
    }

    protected Uri getsUri() {
        return MyContentProvider2.sUri;
    }
}