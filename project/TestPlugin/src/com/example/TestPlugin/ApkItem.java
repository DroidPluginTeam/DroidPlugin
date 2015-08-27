package com.example.TestPlugin;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ApkItem {
    Drawable icon;
    CharSequence title;
    String versionName;
    int versionCode;
    String apkfile;
    PackageInfo packageInfo;

    boolean installing = false;

    ApkItem(Context context, PackageInfo info, String path) {
        PackageManager pm = context.getPackageManager();
        Resources resources = null;
        try {
            resources = getResources(context, path);
        } catch (Exception e) {
        }
        try {
            if (resources != null) {
                icon = resources.getDrawable(info.applicationInfo.icon);
            }
        } catch (Exception e) {
            icon = pm.getDefaultActivityIcon();
        }
        try {
            if (resources != null) {
                title = resources.getString(info.applicationInfo.labelRes);
            }
        } catch (Exception e) {
            title = info.packageName;
        }

        versionName = info.versionName;
        versionCode = info.versionCode;
        apkfile = path;
        packageInfo = info;
    }

    ApkItem(PackageManager pm, PackageInfo info, String path) {
        try {
            icon = pm.getApplicationIcon(info.applicationInfo);
        } catch (Exception e) {
            icon = pm.getDefaultActivityIcon();
        }
        title = pm.getApplicationLabel(info.applicationInfo);
        versionName = info.versionName;
        versionCode = info.versionCode;
        apkfile = path;
        packageInfo = info;
    }

    public static Resources getResources(Context context, String apkPath) throws Exception {
        String PATH_AssetManager = "android.content.res.AssetManager";
        Class assetMagCls = Class.forName(PATH_AssetManager);
        Constructor assetMagCt = assetMagCls.getConstructor((Class[]) null);
        Object assetMag = assetMagCt.newInstance((Object[]) null);
        Class[] typeArgs = new Class[1];
        typeArgs[0] = String.class;
        Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod("addAssetPath",
                typeArgs);
        Object[] valueArgs = new Object[1];
        valueArgs[0] = apkPath;
        assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
        Resources res = context.getResources();
        typeArgs = new Class[3];
        typeArgs[0] = assetMag.getClass();
        typeArgs[1] = res.getDisplayMetrics().getClass();
        typeArgs[2] = res.getConfiguration().getClass();
        Constructor resCt = Resources.class.getConstructor(typeArgs);
        valueArgs = new Object[3];
        valueArgs[0] = assetMag;
        valueArgs[1] = res.getDisplayMetrics();
        valueArgs[2] = res.getConfiguration();
        res = (Resources) resCt.newInstance(valueArgs);
        return res;
    }
}