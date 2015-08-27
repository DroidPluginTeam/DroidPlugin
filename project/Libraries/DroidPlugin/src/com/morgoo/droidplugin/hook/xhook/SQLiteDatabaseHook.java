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

package com.morgoo.droidplugin.hook.xhook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.TextUtils;
import com.morgoo.droidplugin.core.PluginDirHelper;
import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.Hook;

import java.io.File;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/4/1.
 */
public class SQLiteDatabaseHook extends Hook {

    private static final String TAG = "SQLiteDatabaseHook";

    private final String mDataDir;
    private final String mHostDataDir;
    private final String mHostPkg;


    public SQLiteDatabaseHook(Context hostContext) {
        super(hostContext);
        mDataDir = new File(Environment.getDataDirectory(), "data/").getPath();
        mHostDataDir = PluginDirHelper.getContextDataDir(hostContext);
        mHostPkg = hostContext.getPackageName();
    }

    @Override
    protected BaseHookHandle createHookHandle() {
        return null;
    }

    //传入一个路径，比如/data/data/com.xxx.plugin/xxx 会替换成/data/data/插件宿主包名/Plugin/插件包名/data/插件包名
    private String tryReplacePath(String tarDir) {
        //mDataDir=/data/data/
        //mHostDataDir=/data/data/com.example.TestPlugin/
        //mHostPkg=com.example.TestPlugin
        //Old stat([/data/data/com.example.TestPlugin/Plugin/com.qihoo.appstore/apk/base-1.apk])
        //New stat([/data/data/com.example.TestPlugin/Plugin/com.example.TestPlugin/data/com.example.TestPlugin/Plugin/com.qihoo.appstore/apk/base-1.apk])
        if (tarDir != null && tarDir.length() > mDataDir.length() && !TextUtils.equals(tarDir, mDataDir) && tarDir.startsWith(mDataDir)) {
            if (!tarDir.startsWith(mHostDataDir) && !TextUtils.equals(tarDir, mHostDataDir)) {
                String pkg = tarDir.substring(mDataDir.length() + 1);
                int index = pkg.indexOf("/");
                if (index > 0) {
                    pkg = pkg.substring(0, index);
                }
                if (!TextUtils.equals(pkg, mHostPkg)) {
                    tarDir = tarDir.replace(pkg, String.format("%s/Plugin/%s/data/%s", mHostPkg, pkg, pkg));
                    return tarDir;
                }
            }
        }
        return null;
    }

    protected void replace(Object[] args, int index) {
        if (args != null && args.length > index && args[index] instanceof String) {
            String path = (String) args[index];
            String newPath = tryReplacePath(path);
            if (newPath != null) {
                args[index] = newPath;
            }
        }
    }

    @Override
    protected void onInstall(ClassLoader classLoader) throws Throwable {
//        MethodHook callback = new MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                replace(param.args, 0);
//                super.beforeHookedMethod(param);
//            }
//        };
//        ZHook.hookAllMethods(SQLiteDatabase.class, "openOrCreateDatabase", callback);
//        ZHook.hookAllMethods(SQLiteDatabase.class, "openDatabase", callback);

//        if (classLoader != null) {
//            Class clazz = classLoader.loadClass("com.tencent.kingkong.database.SQLiteDatabase");
//            ZHook.hookAllMethods(clazz, "openOrCreateDatabase", callback);
//        }
    }
}
