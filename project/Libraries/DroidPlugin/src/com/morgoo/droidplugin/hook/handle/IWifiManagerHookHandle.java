/*
**        DroidPlugin Project
**
** Copyright(c) 2015 Andy Zhang <zhangyong232@gmail.com>
**
** This file is part of DroidPlugin.
**
** DroidPlugin is free software: you can redistribute it and/or
** modify it under the terms of the GNU Lesser General Public
** License as published by the Free Software Foundation, either
** version 3 of the License, or (at your option) any later version.
**
** DroidPlugin is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
** Lesser General Public License for more details.
**
** You should have received a copy of the GNU Lesser General Public
** License along with DroidPlugin.  If not, see <http://www.gnu.org/licenses/lgpl.txt>
**
**/

package com.morgoo.droidplugin.hook.handle;

import android.content.Context;
import android.text.TextUtils;

import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.HookedMethodHandler;
import com.morgoo.droidplugin.pm.PluginManager;

import java.lang.reflect.Method;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/6/1.
 */
public class IWifiManagerHookHandle extends BaseHookHandle {

    public IWifiManagerHookHandle(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected void init() {
        sHookedMethodHandlers.put("getScanResults", new getScanResults(mHostContext));
        sHookedMethodHandlers.put("getBatchedScanResults", new getBatchedScanResults(mHostContext));
        sHookedMethodHandlers.put("setWifiEnabled", new setWifiEnabled(mHostContext));
    }

    private class IWifiManagerHookedMethodHandler extends HookedMethodHandler {
        public IWifiManagerHookedMethodHandler(Context hostContext) {
            super(hostContext);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //callingPackage
            final int index = 0;
            if (args != null && args.length > index && args[index] instanceof String) {
                String callingPackage = (String) args[index];
                if (!TextUtils.equals(callingPackage, mHostContext.getPackageName())) {
                    args[index] = mHostContext.getPackageName();
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class getScanResults extends IWifiManagerHookedMethodHandler {
        public getScanResults(Context hostContext) {
            super(hostContext);
        }
    }

    private class getBatchedScanResults extends IWifiManagerHookedMethodHandler {
        public getBatchedScanResults(Context hostContext) {
            super(hostContext);
        }
    }

    private class setWifiEnabled extends HookedMethodHandler {
        public setWifiEnabled(Context hostContext) {
            super(hostContext);
        }

        //bugfix 一个外网崩溃
//        Date:2015-08-03 14:05:55
//                ----------------------------------------System Infomation-----------------------------------
//        AppPkgName:com.qihoo.appstore
//        VersionCode:300030241
//        VersionName:3.2.41
//        Debug:false
//        PName:com.qihoo.appstore:PluginP01
//        imei:18b2003ce37bc6fce14f8fa86351732c
//        Board:MSM8974
//        ro.bootloader:unknown
//        ro.product.brand:smartisan
//        ro.product.cpu.abi:armeabi-v7a
//        ro.product.cpu.abi2:armeabi
//        ro.product.device:msm8974sfo_lte
//        ro.build.display.id:SANFRANCISCO dev-keys
//        ro.build.fingerprint:smartisan/msm8974sfo_lte/msm8974sfo_lte:4.4.2/SANFRANCISCO:user/dev-keys
//        ro.hardware:qcom
//        ro.build.host:build4
//        ro.build.id:SANFRANCISCO
//        ro.product.manufacturer:smartisan
//        ro.product.model:SM705
//        ro.product.name:msm8974sfo_lte
//        gsm.version.baseband:2.0.1-00154-M8974AAAAANPZM-L
//        ro.build.tags:dev-keys
//        ro.build.type:user
//        ro.build.user:smartcm
//        ro.build.version.codename:REL
//        ro.build.version.incremental:14
//        ro.build.version.release:4.4.2
//        ro.build.version.sdk:19
//        com.qihoo360.mobilesafe.clean version code :65 version name :1.5
//
//
//
//                ----------------------------------Exception---------------------------------------
//
//
//                ----------------------------Exception message:Unable to start activity ComponentInfo{com.qihoo.appstore.plugin/com.qihoo.appstore.sharenearby.NBReceiverActivity}: java.lang.SecurityException: com.qihoo.appstore.plugin from uid 10082 not allowed to perform WIFI_CHANGE
//
//        ----------------------------Exception StackTrace:
//        java.lang.RuntimeException: Unable to start activity ComponentInfo{com.qihoo.appstore.plugin/com.qihoo.appstore.sharenearby.NBReceiverActivity}: java.lang.SecurityException: com.qihoo.appstore.plugin from uid 10082 not allowed to perform WIFI_CHANGE
//        at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2218)
//        at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:2268)
//        at android.app.ActivityThread.access$800(ActivityThread.java:145)
//        at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1206)
//        at android.os.Handler.dispatchMessage(Handler.java:102)
//        at android.os.Looper.loop(Looper.java:136)
//        at android.app.ActivityThread.main(ActivityThread.java:5086)
//        at java.lang.reflect.Method.invokeNative(Native Method)
//        at java.lang.reflect.Method.invoke(Method.java:515)
//        at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:888)
//        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:697)
//        at dalvik.system.NativeStart.main(Native Method)
//        Caused by: java.lang.SecurityException: com.qihoo.appstore.plugin from uid 10082 not allowed to perform WIFI_CHANGE
//        at android.os.Parcel.readException(Parcel.java:1465)
//        at android.os.Parcel.readException(Parcel.java:1419)
//        at android.net.wifi.IWifiManager$Stub$Proxy.setWifiEnabled(IWifiManager.java:809)
//        at java.lang.reflect.Method.invokeNative(Native Method)
//        at java.lang.reflect.Method.invoke(Method.java:515)
//        at com.morgoo.droidplugin.c.a.a.invoke(Unknown Source)
//        at com.morgoo.droidplugin.c.a.k.invoke(Unknown Source)
//        at $Proxy8.setWifiEnabled(Native Method)
//        at android.net.wifi.WifiManager.setWifiEnabled(WifiManager.java:1028)
//        at com.qihoo.a.m.b(Unknown Source)
//        at com.qihoo.appstore.sharenearby.NBReceiverActivity.onCreate(Unknown Source)
//        at android.app.Activity.performCreate(Activity.java:5231)
//        at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1087)
//        at com.morgoo.droidplugin.c.b.hq.callActivityOnCreate(Unknown Source)
//        at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2172)
//                ... 11 more
//        ============================
//               17个 ，总共10643个,占比0.15972939960537444%

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            if (args != null && args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    Object arg = args[i];
                    if (arg != null && arg instanceof String) {
                        String str = ((String) arg);
                        if (!TextUtils.equals(str, mHostContext.getPackageName()) && PluginManager.getInstance().isPluginPackage(str)) {
                            args[i] = mHostContext.getPackageName();
                        }
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }
}
