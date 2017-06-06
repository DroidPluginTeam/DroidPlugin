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

package com.morgoo.droidplugin.core;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 插件目录结构
 * 基本目录： /data/data/com.HOST.PACKAGE/Plugin
 * 单个插件的基本目录： /data/data/com.HOST.PACKAGE/Plugin
 * source_dir： /data/data/com.HOST.PACKAGE/Plugin/PLUGIN.PKG/apk/apk-1.apk
 * 数据目录： /data/data/com.HOST.PACKAGE/Plugin/PLUGIN.PKG/data/PLUGIN.PKG
 * dex缓存目录： /data/data/com.HOST.PACKAGE/Plugin/PLUGIN.PKG/dalvik-cache/
 * <p>
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/2/5.
 */
public class PluginDirHelper {
    private static final String TAG = PluginDirHelper.class.getSimpleName();

    private static final String DATA_DIR_MODE = "701";
    private static final String DEFAULT_DIR_MODE = "700";
    private static File sBaseDir = null;

    private static void init(Context context) {
        if (sBaseDir == null) {
            sBaseDir = new File(context.getCacheDir().getParentFile(), "Plugin");
//            sBaseDir = context.getDir("plugin", Context.MODE_WORLD_READABLE);
            enforceDirExists(sBaseDir, DATA_DIR_MODE);
        }
    }

    private static String enforceDirExists(File file, String modeStr) {
        if (!file.exists()) {
            file.mkdirs();

            String cmd = "";
            try {
                cmd = "chmod " + modeStr + " " + file.getPath();
//                Log.d(TAG, "cmd: " + cmd);

                ProcessBuilder pb = new ProcessBuilder(new String[]{"chmod", modeStr, file.getPath()});
                pb.start();
            } catch (IOException e) {
                Log.e(TAG, "error in exec cmd: " + cmd, e);
            }
        }
        return file.getPath();

    }

    private static String enforceDirExists(File file) {
        return enforceDirExists(file, DEFAULT_DIR_MODE);
    }

    public static String makePluginBaseDir(Context context, String pluginInfoPackageName) {
        init(context);
        return enforceDirExists(new File(sBaseDir, pluginInfoPackageName), DATA_DIR_MODE);
    }

    public static String getBaseDir(Context context) {
        init(context);
        return enforceDirExists(sBaseDir);
    }

    public static String getPluginDataDir(Context context, String pluginInfoPackageName) {
        String dataDir = enforceDirExists(new File(makePluginBaseDir(context, pluginInfoPackageName), "data"), DATA_DIR_MODE);
        return enforceDirExists(new File(dataDir,pluginInfoPackageName), DATA_DIR_MODE);
    }

    public static String getPluginSignatureDir(Context context, String pluginInfoPackageName) {
        return enforceDirExists(new File(makePluginBaseDir(context, pluginInfoPackageName), "Signature/"));
    }

    public static String getPluginSignatureFile(Context context, String pluginInfoPackageName, int index) {
        return new File(getPluginSignatureDir(context, pluginInfoPackageName), String.format("Signature_%s.key", index)).getPath();
    }

    public static List<String> getPluginSignatureFiles(Context context, String pluginInfoPackageName) {
        ArrayList<String> files = new ArrayList<String>();
        String dir = getPluginSignatureDir(context, pluginInfoPackageName);
        File d = new File(dir);
        File[] fs = d.listFiles();
        if (fs != null && fs.length > 0) {
            for (File f : fs) {
                files.add(f.getPath());
            }
        }
        return files;
    }

    public static String getPluginApkDir(Context context, String pluginInfoPackageName) {
        return enforceDirExists(new File(makePluginBaseDir(context, pluginInfoPackageName), "apk"));
    }

    public static String getPluginApkFile(Context context, String pluginInfoPackageName) {
        return new File(getPluginApkDir(context, pluginInfoPackageName), "base-1.apk").getPath();
    }

    public static String getPluginDalvikCacheDir(Context context, String pluginInfoPackageName) {
        return enforceDirExists(new File(makePluginBaseDir(context, pluginInfoPackageName), "dalvik-cache"));
    }

    public static String getPluginNativeLibraryDir(Context context, String pluginInfoPackageName) {
        return enforceDirExists(new File(makePluginBaseDir(context, pluginInfoPackageName), "lib"));
    }


    public static String getPluginDalvikCacheFile(Context context, String pluginInfoPackageName) {
        String dalvikCacheDir = getPluginDalvikCacheDir(context, pluginInfoPackageName);

        String pluginApkFile = getPluginApkFile(context, pluginInfoPackageName);
        String apkName = new File(pluginApkFile).getName();
        String dexName = apkName.replace(File.separator, "@");
        if (dexName.startsWith("@")) {
            dexName = dexName.substring(1);
        }
        return new File(dalvikCacheDir, dexName + "@classes.dex").getPath();
    }

    public static String getContextDataDir(Context context) {
        String dataDir = new File(Environment.getDataDirectory(), "data/").getPath();
        return new File(dataDir, context.getPackageName()).getPath();
    }

    public static void cleanOptimizedDirectory(String optimizedDirectory) {
        try {
            File dir = new File(optimizedDirectory);
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles();
                if (files != null && files.length > 0) {
                    for (File f : files) {
                        f.delete();
                    }
                }
            }

            if (dir.exists() && dir.isFile()) {
                dir.delete();
                dir.mkdirs();
            }
        } catch (Throwable e) {
        }
    }
}
