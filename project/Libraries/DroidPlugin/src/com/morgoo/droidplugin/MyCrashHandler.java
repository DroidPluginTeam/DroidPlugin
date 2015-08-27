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

package com.morgoo.droidplugin;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Environment;
import com.morgoo.helper.Log;
import com.morgoo.helper.compat.SystemPropertiesCompat;

import java.io.File;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Andy Zhang(zhangyong232@gmail.com)
 */
public class MyCrashHandler implements UncaughtExceptionHandler {

    private static final String TAG = "MyCrashHandler";

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT1 = new SimpleDateFormat("yyyyMMddHHmmss");

    private static final MyCrashHandler sMyCrashHandler = new MyCrashHandler();


    private UncaughtExceptionHandler mOldHandler;

    private Context mContext;


    public static MyCrashHandler getInstance() {
        return sMyCrashHandler;
    }

    public void register(Context context) {
        if (context != null) {
            mOldHandler = Thread.getDefaultUncaughtExceptionHandler();
            if (mOldHandler != this) {
                Thread.setDefaultUncaughtExceptionHandler(this);
            }
            mContext = context;
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.e(TAG, "uncaughtException", ex);
        PrintWriter writer = null;
        try {
            Date date = new Date();
            String dateStr = SIMPLE_DATE_FORMAT1.format(date);

            File file = new File(Environment.getExternalStorageDirectory(), String.format("PluginLog/CrashLog/CrashLog_%s_%s.log", dateStr, android.os.Process.myPid()));
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (file.exists()) {
                file.delete();
            }

            writer = new PrintWriter(file);

            writer.println("Date:" + SIMPLE_DATE_FORMAT.format(date));
            writer.println("----------------------------------------System Infomation-----------------------------------");

            String packageName = mContext.getPackageName();
            writer.println("AppPkgName:" + packageName);
            try {
                PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(packageName, 0);
                writer.println("VersionCode:" + packageInfo.versionCode);
                writer.println("VersionName:" + packageInfo.versionName);
                writer.println("Debug:" + (0 != (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE)));
            } catch (Exception e) {
                writer.println("VersionCode:-1");
                writer.println("VersionName:null");
                writer.println("Debug:Unkown");
            }

            writer.println("PName:" + getProcessName());

            try {
                writer.println("imei:" + getIMEI(mContext));
            } catch (Exception e) {
            }

            writer.println("Board:" + SystemPropertiesCompat.get("ro.product.board", "unknown"));
            writer.println("ro.bootloader:" + SystemPropertiesCompat.get("ro.bootloader", "unknown"));
            writer.println("ro.product.brand:" + SystemPropertiesCompat.get("ro.product.brand", "unknown"));
            writer.println("ro.product.cpu.abi:" + SystemPropertiesCompat.get("ro.product.cpu.abi", "unknown"));
            writer.println("ro.product.cpu.abi2:" + SystemPropertiesCompat.get("ro.product.cpu.abi2", "unknown"));
            writer.println("ro.product.device:" + SystemPropertiesCompat.get("ro.product.device", "unknown"));
            writer.println("ro.build.display.id:" + SystemPropertiesCompat.get("ro.build.display.id", "unknown"));
            writer.println("ro.build.fingerprint:" + SystemPropertiesCompat.get("ro.build.fingerprint", "unknown"));
            writer.println("ro.hardware:" + SystemPropertiesCompat.get("ro.hardware", "unknown"));
            writer.println("ro.build.host:" + SystemPropertiesCompat.get("ro.build.host", "unknown"));
            writer.println("ro.build.id:" + SystemPropertiesCompat.get("ro.build.id", "unknown"));
            writer.println("ro.product.manufacturer:" + SystemPropertiesCompat.get("ro.product.manufacturer", "unknown"));
            writer.println("ro.product.model:" + SystemPropertiesCompat.get("ro.product.model", "unknown"));
            writer.println("ro.product.name:" + SystemPropertiesCompat.get("ro.product.name", "unknown"));
            writer.println("gsm.version.baseband:" + SystemPropertiesCompat.get("gsm.version.baseband", "unknown"));
            writer.println("ro.build.tags:" + SystemPropertiesCompat.get("ro.build.tags", "unknown"));
            writer.println("ro.build.type:" + SystemPropertiesCompat.get("ro.build.type", "unknown"));
            writer.println("ro.build.user:" + SystemPropertiesCompat.get("ro.build.user", "unknown"));
            writer.println("ro.build.version.codename:" + SystemPropertiesCompat.get("ro.build.version.codename", "unknown"));
            writer.println("ro.build.version.incremental:" + SystemPropertiesCompat.get("ro.build.version.incremental", "unknown"));
            writer.println("ro.build.version.release:" + SystemPropertiesCompat.get("ro.build.version.release", "unknown"));
            writer.println("ro.build.version.sdk:" + SystemPropertiesCompat.get("ro.build.version.sdk", "unknown"));
            writer.println("\n\n\n----------------------------------Exception---------------------------------------\n\n");
            writer.println("----------------------------Exception message:" + ex.getLocalizedMessage() + "\n");
            writer.println("----------------------------Exception StackTrace:");
            ex.printStackTrace(writer);
        } catch (Throwable e) {
            Log.e(TAG, "记录uncaughtException", e);
        } finally {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            } catch (Exception e) {
            }

            if (mOldHandler != null) {
                mOldHandler.uncaughtException(thread, ex);
            }
        }
    }

    private String getIMEI(Context mContext) {
        return "test";
    }

    public String getProcessName() {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        for (RunningAppProcessInfo info : infos) {
            if (info.pid == android.os.Process.myPid()) {
                return info.processName;
            }
        }
        return null;
    }
}
