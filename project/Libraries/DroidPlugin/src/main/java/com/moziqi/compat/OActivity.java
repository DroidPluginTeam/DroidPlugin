package com.moziqi.compat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.morgoo.droidplugin.PluginManagerService;

/**
 * Copyright (C), 2018-2020
 * Author: ziqimo
 * Date: 2020/7/31 10:34 PM
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
public class OActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            //设置1像素
            Window window = getWindow();
            window.setGravity(Gravity.LEFT | Gravity.TOP);
            WindowManager.LayoutParams params = window.getAttributes();
            params.x = 0;
            params.y = 0;
            params.height = 1;
            params.width = 1;
            params.gravity = Gravity.BOTTOM;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            window.setAttributes(params);
            //PluginManager.connectToService(PluginManager.java:216
            Intent intent = new Intent(getApplicationContext(), PluginManagerService.class);
            intent.setPackage(getApplicationContext().getPackageName());
            startService(intent);
            //大于5.0
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                //VBJSer.start(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            finish();
        }
    }

}
