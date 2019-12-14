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
import android.os.Environment;
import android.text.TextUtils;
import com.morgoo.droidplugin.core.PluginDirHelper;
import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.HookedMethodHandler;

import java.io.File;
import java.lang.reflect.Method;

/**
 * 重定向所有的IO操作。
 * <p/>
 * Created by Andy Zhang(zhangyong232@gmail.com) 2015/3/30.
 */
public class LibCoreHookHandle extends BaseHookHandle {

    public LibCoreHookHandle(Context hostContext) {
        super(hostContext);
    }

    private static final String TAG = LibCoreHookHandle.class.getSimpleName();

    @Override
    protected void init() {
        sHookedMethodHandlers.put("access", new access(mHostContext));
        sHookedMethodHandlers.put("chmod", new chmod(mHostContext));
        sHookedMethodHandlers.put("chown", new chown(mHostContext));
        sHookedMethodHandlers.put("execv", new execv(mHostContext));
        sHookedMethodHandlers.put("execve", new execve(mHostContext));
        sHookedMethodHandlers.put("mkdir", new mkdir(mHostContext));
        sHookedMethodHandlers.put("open", new open(mHostContext));
        sHookedMethodHandlers.put("remove", new remove(mHostContext));
        sHookedMethodHandlers.put("rename", new rename(mHostContext));
        sHookedMethodHandlers.put("stat", new stat(mHostContext));
        sHookedMethodHandlers.put("statvfs", new statvfs(mHostContext));
        sHookedMethodHandlers.put("symlink", new symlink(mHostContext));
    }

    private abstract static class BaseLibCore extends HookedMethodHandler {

        private final String mDataDir;
        private final String mHostDataDir;
        private final String mHostPkg;

        public BaseLibCore(Context context) {
            super(context);
            mDataDir = new File(Environment.getDataDirectory(), "data/").getPath();
            mHostDataDir = PluginDirHelper.getContextDataDir(context);
            mHostPkg = context.getPackageName();
        }


        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
//            Log.i(TAG, "Old %s(%s)", method.getName(), Arrays.toString(args));
            int index = 0;
            replace(args, index);
//            Log.i(TAG, "New %s(%s)", method.getName(), Arrays.toString(args));
            return super.beforeInvoke(receiver, method, args);
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
    }

    private class access extends BaseLibCore {
        public access(Context context) {
            super(context);
        }
    }

    private class chmod extends BaseLibCore {
        public chmod(Context context) {
            super(context);
        }
    }

    private class chown extends BaseLibCore {
        public chown(Context context) {
            super(context);
        }
    }

    private class execv extends BaseLibCore {
        public execv(Context context) {
            super(context);
        }
    }

    private class execve extends BaseLibCore {
        public execve(Context context) {
            super(context);
        }
    }

    private class mkdir extends BaseLibCore {
        public mkdir(Context context) {
            super(context);
        }
    }

    private class open extends BaseLibCore {
        public open(Context context) {
            super(context);
        }
    }

    private class remove extends BaseLibCore {
        public remove(Context context) {
            super(context);
        }
    }

    private class rename extends BaseLibCore {
        public rename(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            int index = 1;
            replace(args, index);
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class stat extends BaseLibCore {
        public stat(Context context) {
            super(context);
        }
    }

    private class statvfs extends BaseLibCore {
        public statvfs(Context context) {
            super(context);
        }
    }

    private class symlink extends BaseLibCore {
        public symlink(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            int index = 1;
            replace(args, index);
            return super.beforeInvoke(receiver, method, args);
        }
    }
}
