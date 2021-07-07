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

import android.app.AppOpsManager;
import android.content.Context;
import android.util.Log;

import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.HookedMethodHandler;
//import com.morgoo.helper.Log;
import com.morgoo.helper.compat.IAppOpsServiceCompat;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on  on 16/5/11.
 */
public class IAppOpsServiceHookHandle extends BaseHookHandle {

    public IAppOpsServiceHookHandle(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected void init() {
//        interface IAppOpsService {
//            int checkOperation(int code, int uid, String packageName);
//            int noteOperation(int code, int uid, String packageName);
//            int startOperation(IBinder token, int code, int uid, String packageName);
//            void finishOperation(IBinder token, int code, int uid, String packageName);
//            void startWatchingMode(int op, String packageName, IAppOpsCallback callback);
//            void stopWatchingMode(IAppOpsCallback callback);
//            IBinder getToken(IBinder clientToken);
//            int permissionToOpCode(String permission);
//            int noteProxyOperation(int code, String proxyPackageName,int callingUid, String callingPackageName);
//            int checkPackage(int uid, String packageName);
//            List<AppOpsManager.PackageOps> getPackagesForOps(in int[] ops);
//            List<AppOpsManager.PackageOps> getOpsForPackage(int uid, String packageName, in int[] ops);
//            void setUidMode(int code, int uid, int mode);
//            void setMode(int code, int uid, String packageName, int mode);
//            void resetAllModes(int reqUserId, String reqPackageName);
//            int checkAudioOperation(int code, int usage, int uid, String packageName);
//            void setAudioRestriction(int code, int usage, int uid, int mode, in String[] exceptionPackages);
//            void setUserRestrictions(in Bundle restrictions, int userHandle);
//            void removeUser(int userHandle);
//        }
        sHookedMethodHandlers.put("checkOperation", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("noteOperation", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("startOperation", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("finishOperation", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("startWatchingMode", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("stopWatchingMode", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getToken", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("permissionToOpCode", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("noteProxyOperation", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("checkPackage", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getPackagesForOps", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getOpsForPackage", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("setUidMode", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("setMode", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("resetAllModes", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("checkAudioOperation", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("setAudioRestriction", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("setUserRestrictions", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("removeUser", new MyBaseHandler(mHostContext));
        addAllMethodFromHookedClass();
    }

    @Override
    protected Class<?> getHookedClass() throws ClassNotFoundException {
        return IAppOpsServiceCompat.Class();
    }

    @Override
    protected HookedMethodHandler newBaseHandler() throws ClassNotFoundException {
        return new MyBaseHandler(mHostContext);
    }

    private static class MyBaseHandler extends ReplaceCallingPackageHookedMethodHandler {
        public MyBaseHandler(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //   final int mode = getAppOpsService().noteOperation(AppOpsManager.OP_SYSTEM_ALERT_WINDOW,
            //                callingUid, callingPackage);
            /**
             * ActivityStarter.java
             *
             * //shouldAbortBackgroundActivityStart 方法里面的
             *
             * // don't abort if the callingUid has SYSTEM_ALERT_WINDOW permission
             *         if (mService.hasSystemAlertWindowPermission(callingUid, callingPid, callingPackage)) {
             *             Slog.w(TAG, "Background activity start for " + callingPackage
             *                     + " allowed because SYSTEM_ALERT_WINDOW permission is granted.");
             *             return false;
             *         }
             *
             * //ActivityTaskManagerService.java
             *     boolean hasSystemAlertWindowPermission(int callingUid, int callingPid, String callingPackage) {
             *         final int mode = getAppOpsService().noteOperation(AppOpsManager.OP_SYSTEM_ALERT_WINDOW,
             *                 callingUid, callingPackage);
             *         if (mode == AppOpsManager.MODE_DEFAULT) {
             *             return checkPermission(Manifest.permission.SYSTEM_ALERT_WINDOW, callingPid, callingUid)
             *                     == PERMISSION_GRANTED;
             *         }
             *         return mode == AppOpsManager.MODE_ALLOWED;
             *     }
             */
            Log.i("MyBaseHandler", "method:" + method.getName());
            if ("noteOperation".equals(method.getName())) {
                Log.i("MyBaseHandler", "args:" + Arrays.toString(args));
                //认为有悬浮权限
                setFakedResult(AppOpsManager.MODE_ALLOWED);
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }
}
