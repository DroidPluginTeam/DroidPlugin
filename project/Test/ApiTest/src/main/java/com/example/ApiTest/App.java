package com.example.ApiTest;

import android.app.Application;
import android.util.Log;

/**
 * Copyright (C), 2018-2020
 * Author: ziqimo
 * Date: 2020/8/1 1:52 PM
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("moziqi", "App.onCreate");
    }
}
