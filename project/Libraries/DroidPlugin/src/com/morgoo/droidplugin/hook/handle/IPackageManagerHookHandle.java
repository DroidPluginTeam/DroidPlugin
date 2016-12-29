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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Parcelable;
import android.os.RemoteException;
import android.text.TextUtils;

import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.HookedMethodHandler;
import com.morgoo.droidplugin.pm.PluginManager;
import com.morgoo.droidplugin.reflect.MethodUtils;
import com.morgoo.helper.Log;
import com.morgoo.helper.compat.IPackageDataObserverCompat;
import com.morgoo.helper.compat.ParceledListSliceCompat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/2/28.
 */
public class IPackageManagerHookHandle extends BaseHookHandle {

    private static final String TAG = IPackageManagerHookHandle.class.getSimpleName();

    public IPackageManagerHookHandle(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected void init() {
        sHookedMethodHandlers.put("getPackageInfo", new getPackageInfo(mHostContext));
        sHookedMethodHandlers.put("getPackageUid", new getPackageUid(mHostContext));
        sHookedMethodHandlers.put("getPackageGids", new getPackageGids(mHostContext));
        sHookedMethodHandlers.put("currentToCanonicalPackageNames", new currentToCanonicalPackageNames(mHostContext));
        sHookedMethodHandlers.put("canonicalToCurrentPackageNames", new canonicalToCurrentPackageNames(mHostContext));
        sHookedMethodHandlers.put("getPermissionInfo", new getPermissionInfo(mHostContext));
        sHookedMethodHandlers.put("queryPermissionsByGroup", new queryPermissionsByGroup(mHostContext));
        sHookedMethodHandlers.put("getPermissionGroupInfo", new getPermissionGroupInfo(mHostContext));
        sHookedMethodHandlers.put("getAllPermissionGroups", new getAllPermissionGroups(mHostContext));
        sHookedMethodHandlers.put("getApplicationInfo", new getApplicationInfo(mHostContext));
        sHookedMethodHandlers.put("getActivityInfo", new getActivityInfo(mHostContext));
        sHookedMethodHandlers.put("getReceiverInfo", new getReceiverInfo(mHostContext));
        sHookedMethodHandlers.put("getServiceInfo", new getServiceInfo(mHostContext));
        sHookedMethodHandlers.put("getProviderInfo", new getProviderInfo(mHostContext));
        sHookedMethodHandlers.put("checkPermission", new checkPermission(mHostContext));
        sHookedMethodHandlers.put("checkUidPermission", new checkUidPermission(mHostContext));
        sHookedMethodHandlers.put("addPermission", new addPermission(mHostContext));
        sHookedMethodHandlers.put("removePermission", new removePermission(mHostContext));
        sHookedMethodHandlers.put("grantPermission", new grantPermission(mHostContext));
        sHookedMethodHandlers.put("revokePermission", new revokePermission(mHostContext));
        sHookedMethodHandlers.put("checkSignatures", new checkSignatures(mHostContext));
        sHookedMethodHandlers.put("getPackagesForUid", new getPackagesForUid(mHostContext));
        sHookedMethodHandlers.put("getNameForUid", new getNameForUid(mHostContext));
        sHookedMethodHandlers.put("getUidForSharedUser", new getUidForSharedUser(mHostContext));
        sHookedMethodHandlers.put("getFlagsForUid", new getFlagsForUid(mHostContext));
        sHookedMethodHandlers.put("resolveIntent", new resolveIntent(mHostContext));
        sHookedMethodHandlers.put("queryIntentActivities", new queryIntentActivities(mHostContext));
        sHookedMethodHandlers.put("queryIntentActivityOptions", new queryIntentActivityOptions(mHostContext));
        sHookedMethodHandlers.put("queryIntentReceivers", new queryIntentReceivers(mHostContext));
        sHookedMethodHandlers.put("resolveService", new resolveService(mHostContext));
        sHookedMethodHandlers.put("queryIntentServices", new queryIntentServices(mHostContext));
        sHookedMethodHandlers.put("queryIntentContentProviders", new queryIntentContentProviders(mHostContext));
        sHookedMethodHandlers.put("getInstalledPackages", new getInstalledPackages(mHostContext));
        sHookedMethodHandlers.put("getPackagesHoldingPermissions", new getPackagesHoldingPermissions(mHostContext));
        sHookedMethodHandlers.put("getInstalledApplications", new getInstalledApplications(mHostContext));
        sHookedMethodHandlers.put("getPersistentApplications", new getPersistentApplications(mHostContext));
        sHookedMethodHandlers.put("resolveContentProvider", new resolveContentProvider(mHostContext));
        sHookedMethodHandlers.put("querySyncProviders", new querySyncProviders(mHostContext));
        sHookedMethodHandlers.put("queryContentProviders", new queryContentProviders(mHostContext));
        sHookedMethodHandlers.put("getInstrumentationInfo", new getInstrumentationInfo(mHostContext));
        sHookedMethodHandlers.put("queryInstrumentation", new queryInstrumentation(mHostContext));
        sHookedMethodHandlers.put("getInstallerPackageName", new getInstallerPackageName(mHostContext));
        sHookedMethodHandlers.put("addPackageToPreferred", new addPackageToPreferred(mHostContext));
        sHookedMethodHandlers.put("removePackageFromPreferred", new removePackageFromPreferred(mHostContext));
        sHookedMethodHandlers.put("getPreferredPackages", new getPreferredPackages(mHostContext));
        sHookedMethodHandlers.put("resetPreferredActivities", new resetPreferredActivities(mHostContext));
        sHookedMethodHandlers.put("getLastChosenActivity", new getLastChosenActivity(mHostContext));
        sHookedMethodHandlers.put("setLastChosenActivity", new setLastChosenActivity(mHostContext));
        sHookedMethodHandlers.put("addPreferredActivity", new addPreferredActivity(mHostContext));
        sHookedMethodHandlers.put("replacePreferredActivity", new replacePreferredActivity(mHostContext));
        sHookedMethodHandlers.put("clearPackagePreferredActivities", new clearPackagePreferredActivities(mHostContext));
        sHookedMethodHandlers.put("getPreferredActivities", new getPreferredActivities(mHostContext));
        sHookedMethodHandlers.put("getHomeActivities", new getHomeActivities(mHostContext));
        sHookedMethodHandlers.put("setComponentEnabledSetting", new setComponentEnabledSetting(mHostContext));
        sHookedMethodHandlers.put("getComponentEnabledSetting", new getComponentEnabledSetting(mHostContext));
        sHookedMethodHandlers.put("setApplicationEnabledSetting", new setApplicationEnabledSetting(mHostContext));
        sHookedMethodHandlers.put("getApplicationEnabledSetting", new getApplicationEnabledSetting(mHostContext));
        sHookedMethodHandlers.put("setPackageStoppedState", new setPackageStoppedState(mHostContext));
        sHookedMethodHandlers.put("deleteApplicationCacheFiles", new deleteApplicationCacheFiles(mHostContext));
        sHookedMethodHandlers.put("clearApplicationUserData", new clearApplicationUserData(mHostContext));
        sHookedMethodHandlers.put("getPackageSizeInfo", new getPackageSizeInfo(mHostContext));
        sHookedMethodHandlers.put("performDexOpt", new performDexOpt(mHostContext));
        sHookedMethodHandlers.put("movePackage", new movePackage(mHostContext));
    }

    private class getPackageInfo extends HookedMethodHandler {
        public getPackageInfo(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 2.3,  4.01, 4.0.3_r1
            /*public PackageInfo getPackageInfo(String packageName, int flags) throws RemoteException;*/

            //API 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
         /*public PackageInfo getPackageInfo(String packageName, int flags, int userId) throws RemoteException;*/
            if (args != null) {
                final int index0 = 0, index1 = 1;
                String packageName = null;
                if (args.length > index0) {
                    if (args[index0] != null && args[index0] instanceof String) {
                        packageName = (String) args[index0];
                    }
                }

                int flags = 0;
                if (args.length > index1) {
                    if (args[index1] != null && args[index1] instanceof Integer) {
                        flags = (Integer) args[index1];
                    }
                }

                if (packageName != null) {
                    PackageInfo packageInfo = null;
                    try {
                        packageInfo = PluginManager.getInstance().getPackageInfo(packageName, flags);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (packageInfo != null) {
                        setFakedResult(packageInfo);
                        return true;
                    } else {
                        Log.i(TAG, "getPackageInfo(%s) fail,pkginfo is null", packageName);
                    }
                }

            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class getPackageUid extends HookedMethodHandler {
        public getPackageUid(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 2.3, 4.01_r1, 4.0.3_r1
            /*public int getPackageUid(String packageName) throws RemoteException;*/
            //API 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1,  5.0.2_r1
            /*public int getPackageUid(String packageName, int userId) throws RemoteException;*/
            if (args != null) {
                final int index0 = 0;
                String packageName = null;
                if (args.length > index0) {
                    if (args[index0] != null && args[index0] instanceof String) {
                        packageName = (String) args[index0];
                    }
                    if (packageName != null && PluginManager.getInstance().isPluginPackage(packageName)) {
                        args[index0] = mHostContext.getPackageName();
                    }
                }

            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class getPackageGids extends HookedMethodHandler {
        public getPackageGids(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 2.3, 4.01_r1, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
            /*public int[] getPackageGids(String packageName) throws RemoteException;*/
            if (args != null) {
                final int index0 = 0;
                String packageName = null;
                if (args.length > index0) {
                    if (args[index0] != null && args[index0] instanceof String) {
                        packageName = (String) args[index0];
                    }
                    if (packageName != null && PluginManager.getInstance().isPluginPackage(packageName)) {
                        args[index0] = mHostContext.getPackageName();
                    }
                }

            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class currentToCanonicalPackageNames extends HookedMethodHandler {
        public currentToCanonicalPackageNames(Context context) {
            super(context);
        }
        //API 2.3, 4.01_r1, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*public String[] currentToCanonicalPackageNames(String[] names) throws RemoteException;*/
    }

    private class canonicalToCurrentPackageNames extends HookedMethodHandler {
        public canonicalToCurrentPackageNames(Context context) {
            super(context);
        }

        //API 2.3, 4.01_r1, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*public String[] canonicalToCurrentPackageNames(String[] names) throws RemoteException;*/
    }

    private class getPermissionInfo extends HookedMethodHandler {
        public getPermissionInfo(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 2.3, 4.01_r1, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
              /*public PermissionInfo getPermissionInfo(String name, int flags) throws RemoteException;*/
            if (args != null) {
                final int index0 = 0, index1 = 1;
                if (args.length > 1 && args[index0] instanceof String && args[index1] instanceof Integer) {
                    String packageName = (String) args[index0];
                    int flags = (Integer) args[index1];
                    PermissionInfo permissionInfo = PluginManager.getInstance().getPermissionInfo(packageName, flags);
                    if (permissionInfo != null) {
                        setFakedResult(permissionInfo);
                        return true;
                    }
                }
            }

            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class queryPermissionsByGroup extends HookedMethodHandler {
        public queryPermissionsByGroup(Context context) {
            super(context);
        }

        @Override
        protected void afterInvoke(Object receiver, Method method, Object[] args, Object invokeResult) throws Throwable {
            //API 2.3, 4.01_r1, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
            /*public List<PermissionInfo> queryPermissionsByGroup(String group, int flags) throws RemoteException;*/
            if (args != null && invokeResult instanceof List) {
                final int index0 = 0, index1 = 1;
                if (args.length > 1 && args[index0] instanceof String && args[index1] instanceof Integer) {
                    String group = (String) args[index0];
                    int flags = (Integer) args[index1];
                    List<PermissionInfo> infos = PluginManager.getInstance().queryPermissionsByGroup(group, flags);
                    if (infos != null && infos.size() > 0) {
                        List old = (List) invokeResult;
                        old.addAll(infos);
                    }
                }
            }
            super.afterInvoke(receiver, method, args, invokeResult);
        }
    }

    private class getPermissionGroupInfo extends HookedMethodHandler {
        public getPermissionGroupInfo(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 2.3, 4.01_r1, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*public PermissionGroupInfo getPermissionGroupInfo(String name, int flags) throws RemoteException;*/
            if (args != null) {
                final int index0 = 0, index1 = 1;
                if (args.length > 1 && args[index0] instanceof String && args[index1] instanceof Integer) {
                    String name = (String) args[index0];
                    int flags = (Integer) args[index1];
                    PermissionGroupInfo permissionInfo = PluginManager.getInstance().getPermissionGroupInfo(name, flags);
                    if (permissionInfo != null) {
                        setFakedResult(permissionInfo);
                        return true;
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class getAllPermissionGroups extends HookedMethodHandler {
        public getAllPermissionGroups(Context context) {
            super(context);
        }

        @Override
        protected void afterInvoke(Object receiver, Method method, Object[] args, Object invokeResult) throws Throwable {
            //API 2.3, 4.01_r1, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*  public List<PermissionGroupInfo> getAllPermissionGroups(int flags) throws RemoteException;*/
            if (args != null && invokeResult instanceof List) {
                final int index = 0;
                if (args.length > index && args[index] instanceof Integer) {
                    int flags = (Integer) args[index];
                    List<PermissionGroupInfo> infos = PluginManager.getInstance().getAllPermissionGroups(flags);
                    if (infos != null && infos.size() > 0) {
                        List old = (List) invokeResult;
                        old.addAll(infos);
                    }
                }
            }
            super.afterInvoke(receiver, method, args, invokeResult);
        }
    }

    private class getApplicationInfo extends HookedMethodHandler {
        public getApplicationInfo(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 2.3, 4.01, 4.0.3_r1
        /* public ApplicationInfo getApplicationInfo(String packageName, int flags) throws RemoteException;*/
            //API 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /* public ApplicationInfo getApplicationInfo(String packageName, int flags, int userId) throws RemoteException;*/
            if (args != null) {
                final int index0 = 0, index1 = 1;
                if (args.length >= 2 && args[index0] instanceof String && args[index1] instanceof Integer) {
                    String packageName = (String) args[index0];
                    int flags = (Integer) args[index1];
                    ApplicationInfo info = PluginManager.getInstance().getApplicationInfo(packageName, flags);
                    if (info != null) {
                        setFakedResult(info);
                        return true;
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class getActivityInfo extends HookedMethodHandler {
        public getActivityInfo(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 2.3, 4.01, 4.0.3_r1
        /* public ActivityInfo getActivityInfo(ComponentName className, int flags) throws RemoteException;*/
            //API 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*public ActivityInfo getActivityInfo(ComponentName className, int flags, int userId) throws RemoteException;*/
            if (args != null) {
                final int index0 = 0, index1 = 1;
                if (args.length >= 2 && args[index0] instanceof ComponentName && args[index1] instanceof Integer) {
                    ComponentName className = (ComponentName) args[index0];
                    int flags = (Integer) args[index1];
                    ActivityInfo info = PluginManager.getInstance().getActivityInfo(className, flags);
                    if (info != null) {
                        setFakedResult(info);
                        return true;
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class getReceiverInfo extends HookedMethodHandler {
        public getReceiverInfo(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 2.3, 4.01, 4.0.3_r1
        /*public ActivityInfo getReceiverInfo(ComponentName className, int flags) throws RemoteException;*/
            //API 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*public ActivityInfo getReceiverInfo(ComponentName className, int flags, int userId) throws RemoteException;*/
            if (args != null) {
                final int index0 = 0, index1 = 1;
                if (args.length >= 2 && args[index0] instanceof ComponentName && args[index1] instanceof Integer) {
                    ComponentName className = (ComponentName) args[index0];
                    int flags = (Integer) args[index1];
                    ActivityInfo info = PluginManager.getInstance().getReceiverInfo(className, flags);
                    if (info != null) {
                        setFakedResult(info);
                        return true;
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class getServiceInfo extends HookedMethodHandler {
        public getServiceInfo(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 2.3, 4.01, 4.0.3_r1
        /* public ServiceInfo getServiceInfo(ComponentName className, int flags) throws RemoteException;*/
            //API 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /* public ServiceInfo getServiceInfo(ComponentName className, int flags, int userId) throws RemoteException;*/
            if (args != null) {
                final int index0 = 0, index1 = 1;
                if (args.length >= 2 && args[index0] instanceof ComponentName && args[index1] instanceof Integer) {
                    ComponentName className = (ComponentName) args[index0];
                    int flags = (Integer) args[index1];
                    ServiceInfo info = PluginManager.getInstance().getServiceInfo(className, flags);
                    if (info != null) {
                        setFakedResult(info);
                        return true;
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class getProviderInfo extends HookedMethodHandler {
        public getProviderInfo(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 2.3
        /*public ProviderInfo getProviderInfo(ComponentName className, int flags) throws RemoteException;*/
            //API 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /* public ProviderInfo getProviderInfo(ComponentName className, int flags, int userId) throws RemoteException;*/
            if (args != null) {
                final int index0 = 0, index1 = 1;
                if (args.length >= 2 && args[index0] instanceof ComponentName && args[index1] instanceof Integer) {
                    ComponentName className = (ComponentName) args[index0];
                    int flags = (Integer) args[index1];
                    ProviderInfo info = PluginManager.getInstance().getProviderInfo(className, flags);
                    if (info != null) {
                        setFakedResult(info);
                        return true;
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class checkPermission extends HookedMethodHandler {
        public checkPermission(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 2.3, 4.01_r1, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*public int checkPermission(String permName, String pkgName) throws RemoteException;*/
            if (args != null) {
                final int index = 1;
                if (args.length > index && args[index] instanceof String) {
                    String packageName = (String) args[index];
                    if (PluginManager.getInstance().isPluginPackage(packageName)) {
                        args[index] = mHostContext.getPackageName();
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class checkUidPermission extends HookedMethodHandler {
        public checkUidPermission(Context context) {
            super(context);
        }
        //API 2.3, 4.01_r1, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*public int checkUidPermission(String permName, int uid) throws RemoteException;*/
    }

    private class addPermission extends HookedMethodHandler {
        public addPermission(Context context) {
            super(context);
        }

        //API 2.3, 4.01_r1, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*public boolean addPermission(PermissionInfo info) throws RemoteException;*/
    }

    private class removePermission extends HookedMethodHandler {
        public removePermission(Context context) {
            super(context);
        }

        //API 2.3, 4.01_r1, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*public void removePermission(String name) throws RemoteException;*/
    }

    private class grantPermission extends HookedMethodHandler {
        public grantPermission(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 2.3, 4.01, 4.0.3_r1
            //NO
            //API 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /* public void grantPermission(String packageName, String permissionName) throws RemoteException;*/
            if (args != null) {
                final int index = 0;
                if (args.length > index && args[index] instanceof String) {
                    String packageName = (String) args[index];
                    if (PluginManager.getInstance().isPluginPackage(packageName)) {
                        args[index] = mHostContext.getPackageName();
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class revokePermission extends HookedMethodHandler {
        public revokePermission(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 2.3, 4.01, 4.0.3_r1
            //NO
            //API 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*public void revokePermission(String packageName, String permissionName) throws RemoteException;*/
            if (args != null) {
                final int index = 0;
                if (args.length > index && args[index] instanceof String) {
                    String packageName = (String) args[index];
                    if (PluginManager.getInstance().isPluginPackage(packageName)) {
                        args[index] = mHostContext.getPackageName();
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class checkSignatures extends HookedMethodHandler {
        public checkSignatures(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 2.3, 4.01_r1, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /* public int checkSignatures(String pkg1, String pkg2) throws android.os.RemoteException;*/
            final int index0 = 0, index1 = 1;
            String pkg0 = null, pkg1 = null;
            if (args != null && args[index0] != null && args[index0] instanceof String) {
                pkg0 = (String) args[index0];
            }

            if (args != null && args[index1] != null && args[index1] instanceof String) {
                pkg1 = (String) args[index1];
            }

            if (!TextUtils.isEmpty(pkg0) && !TextUtils.isEmpty(pkg1)) {
                PluginManager instance = PluginManager.getInstance();
                if (instance.isPluginPackage(pkg0) && instance.isPluginPackage(pkg1)) {
                    int result = instance.checkSignatures(pkg0, pkg1);
                    setFakedResult(result);
                    return true;
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class getPackagesForUid extends HookedMethodHandler {
        public getPackagesForUid(Context context) {
            super(context);
        }
        //API 2.3, 4.01_r1, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /* public  String[] getPackagesForUid(int uid) throws RemoteException*/
    }

    private class getNameForUid extends HookedMethodHandler {
        public getNameForUid(Context context) {
            super(context);
        }
        //API 2.3, 4.01_r1, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*public String getNameForUid(int uid) throws RemoteException;*/
    }

    private class getUidForSharedUser extends HookedMethodHandler {
        public getUidForSharedUser(Context context) {
            super(context);
        }
        //API 2.3, 4.01_r1, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*  public int getUidForSharedUser(String sharedUserName) throws RemoteException;*/
    }

    private class getFlagsForUid extends HookedMethodHandler {
        public getFlagsForUid(Context context) {
            super(context);
        }
        //API 2.3, 4.01, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1
//        NO
        //API 4.4_r1, 5.0.2_r1
        /*public int getFlagsForUid(int uid) throws android.os.RemoteException;*/
    }

    private class resolveIntent extends HookedMethodHandler {
        public resolveIntent(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 2.3, 4.01, 4.0.3_r1
        /* public ResolveInfo resolveIntent(Intent intent, String resolvedType, int flags) throws RemoteException;*/
            //API 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /* public ResolveInfo resolveIntent(Intent intent, String resolvedType, int flags, int userId) throws RemoteException;*/
            if (args != null) {
                final int index0 = 0, index1 = 1, index2 = 2;
                Intent intent = null;
                if (args.length > index0) {
                    if (args[index0] instanceof Intent) {
                        intent = (Intent) args[index0];
                    }
                }

                String resolvedType = null;
                if (args.length > index1) {
                    if (args[index1] instanceof String) {
                        resolvedType = (String) args[index1];
                    }
                }

                Integer flags = 0;
                if (args.length > index2) {
                    if (args[index2] instanceof Integer) {
                        flags = (Integer) args[index2];
                    }
                }

                if (intent != null) {
                    ResolveInfo info = PluginManager.getInstance().resolveIntent(intent, resolvedType, flags);
                    if (info != null) {
                        setFakedResult(info);
                        return true;
                    }
                }

            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class queryIntentActivities extends HookedMethodHandler {
        public queryIntentActivities(Context context) {
            super(context);
        }

        @Override
        protected void afterInvoke(Object receiver, Method method, Object[] args, Object invokeResult) throws Throwable {
            //API 2.3, 4.01, 4.0.3_r1
        /* public List<ResolveInfo> queryIntentActivities(Intent intent, String resolvedType, int flags) throws RemoteException;*/
            //API 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /* public List<ResolveInfo> queryIntentActivities(Intent intent, String resolvedType, int flags, int userId) throws RemoteException;*/
            if (args != null && (invokeResult instanceof List || ParceledListSliceCompat.isParceledListSlice(invokeResult))) {
                final int index0 = 0, index1 = 1, index2 = 2;
                Intent intent = null;
                if (args.length > index0) {
                    if (args[index0] instanceof Intent) {
                        intent = (Intent) args[index0];
                    }
                }

                String resolvedType = null;
                if (args.length > index1) {
                    if (args[index1] instanceof String) {
                        resolvedType = (String) args[index1];
                    }
                }

                Integer flags = 0;
                if (args.length > index2) {
                    if (args[index2] instanceof Integer) {
                        flags = (Integer) args[index2];
                    }
                }

                if (intent != null) {
                    List<ResolveInfo> infos = PluginManager.getInstance().queryIntentActivities(intent, resolvedType, flags);
                    if (infos != null && infos.size() > 0) {
                        if (invokeResult instanceof List) {
                            List old = (List) invokeResult;
                            old.addAll(infos);
                        } else if (ParceledListSliceCompat.isParceledListSlice(invokeResult)) {
                            if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2) { //Only for api 24
                                Method getListMethod = MethodUtils.getAccessibleMethod(invokeResult.getClass(), "getList");
                                List data = (List) getListMethod.invoke(invokeResult);
                                data.addAll(infos);
                            }
                        }
                    }
                }
            }
            super.afterInvoke(receiver, method, args, invokeResult);
        }
    }

    private class queryIntentActivityOptions extends HookedMethodHandler {
        public queryIntentActivityOptions(Context context) {
            super(context);
        }
        //API 2.3, 4.01, 4.0.3_r1
        /*public List<ResolveInfo> queryIntentActivityOptions(ComponentName caller, Intent[] specifics, String[] specificTypes, Intent intent, String resolvedType, int flags) throws RemoteException;*/
        //API 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /* public List<ResolveInfo> queryIntentActivityOptions(ComponentName caller, Intent[] specifics, String[] specificTypes, Intent intent, String resolvedType, int flags, int userId) throws RemoteException;*/
        //TODO 这里需要实现。查询插件的结果，并入到返回值中。
    }

    private class queryIntentReceivers extends HookedMethodHandler {
        public queryIntentReceivers(Context context) {
            super(context);
        }

        @Override
        protected void afterInvoke(Object receiver, Method method, Object[] args, Object invokeResult) throws Throwable {
            //API 2.3, 4.01, 4.0.3_r1
        /*  public List<ResolveInfo> queryIntentReceivers(Intent intent, String resolvedType, int flags) throws RemoteException;*/
            //API 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*public List<ResolveInfo> queryIntentReceivers(Intent intent, String resolvedType, int flags, int userId) throws RemoteException;*/
            if (args != null && invokeResult instanceof List) {
                final int index0 = 0, index1 = 1, index2 = 2;
                Intent intent = null;
                if (args.length > index0) {
                    if (args[index0] instanceof Intent) {
                        intent = (Intent) args[index0];
                    }
                }

                String resolvedType = null;
                if (args.length > index1) {
                    if (args[index1] instanceof String) {
                        resolvedType = (String) args[index1];
                    }
                }

                Integer flags = 0;
                if (args.length > index2) {
                    if (args[index2] instanceof Integer) {
                        flags = (Integer) args[index2];
                    }
                }

                if (intent != null) {
                    List<ResolveInfo> infos = PluginManager.getInstance().queryIntentReceivers(intent, resolvedType, flags);
                    if (infos != null && infos.size() > 0) {
                        List old = (List) invokeResult;
                        old.addAll(infos);
                        setFakedResult(invokeResult);
                    }
                }
            }
            super.afterInvoke(receiver, method, args, invokeResult);
        }
    }

    private class resolveService extends HookedMethodHandler {
        public resolveService(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 2.3, 4.01, 4.0.3_r1
        /* public ResolveInfo resolveService(Intent intent, String resolvedType, int flags) throws RemoteException;*/
            //API 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /* public ResolveInfo resolveService(Intent intent, String resolvedType, int flags, int userId) throws RemoteException;*/
            if (args != null) {
                final int index0 = 0, index1 = 1, index2 = 2;
                Intent intent = null;
                if (args.length > index0) {
                    if (args[index0] instanceof Intent) {
                        intent = (Intent) args[index0];
                    }
                }

                String resolvedType = null;
                if (args.length > index1) {
                    if (args[index1] instanceof String) {
                        resolvedType = (String) args[index1];
                    }
                }

                Integer flags = 0;
                if (args.length > index2) {
                    if (args[index2] instanceof Integer) {
                        flags = (Integer) args[index2];
                    }
                }

                if (intent != null) {
                    ResolveInfo info = PluginManager.getInstance().resolveService(intent, resolvedType, flags);
                    if (info != null) {
                        setFakedResult(info);
                        return true;
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class queryIntentServices extends HookedMethodHandler {
        public queryIntentServices(Context context) {
            super(context);
        }

        @Override
        protected void afterInvoke(Object receiver, Method method, Object[] args, Object invokeResult) throws Throwable {
            //API 2.3
        /*public List<ResolveInfo> queryIntentServices(Intent intent, String resolvedType, int flags) throws RemoteException;*/
            //API 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /* public List<ResolveInfo> queryIntentServices(Intent intent, String resolvedType, int flags, int userId) throwsRemoteException;*/
            if (args != null && invokeResult instanceof List) {
                final int index0 = 0, index1 = 1, index2 = 2;
                Intent intent = null;
                if (args.length > index0) {
                    if (args[index0] instanceof Intent) {
                        intent = (Intent) args[index0];
                    }
                }

                String resolvedType = null;
                if (args.length > index1) {
                    if (args[index1] instanceof String) {
                        resolvedType = (String) args[index1];
                    }
                }

                Integer flags = 0;
                if (args.length > index2) {
                    if (args[index2] instanceof Integer) {
                        flags = (Integer) args[index2];
                    }
                }

                if (intent != null) {
                    List<ResolveInfo> infos = PluginManager.getInstance().queryIntentServices(intent, resolvedType, flags);
                    if (infos != null && infos.size() > 0) {
                        List old = (List) invokeResult;
                        old.addAll(infos);
                    }
                }
            }
            super.afterInvoke(receiver, method, args, invokeResult);
        }
    }

    private class queryIntentContentProviders extends HookedMethodHandler {
        public queryIntentContentProviders(Context context) {
            super(context);
        }

        @Override
        protected void afterInvoke(Object receiver, Method method, Object[] args, Object invokeResult) throws Throwable {
            //ONLY FOR API 4.4_r1, 5.0.2_r1
        /*public List<ResolveInfo> queryIntentContentProviders(Intent intent, String resolvedType, int flags, int userId) throws RemoteException;*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (args != null && invokeResult instanceof List) {
                    final int index0 = 0, index1 = 1, index2 = 2;
                    Intent intent = null;
                    if (args.length > index0) {
                        if (args[index0] instanceof Intent) {
                            intent = (Intent) args[index0];
                        }
                    }

                    String resolvedType = null;
                    if (args.length > index1) {
                        if (args[index1] instanceof String) {
                            resolvedType = (String) args[index1];
                        }
                    }

                    Integer flags = 0;
                    if (args.length > index2) {
                        if (args[index2] instanceof Integer) {
                            flags = (Integer) args[index2];
                        }
                    }

                    if (intent != null) {
                        List<ResolveInfo> infos = PluginManager.getInstance().queryIntentContentProviders(intent, resolvedType, flags);
                        if (infos != null && infos.size() > 0) {
                            List old = (List) invokeResult;
                            old.addAll(infos);
                        }
                    }
                }
            }
            super.afterInvoke(receiver, method, args, invokeResult);
        }
    }

    private class getInstalledPackages extends HookedMethodHandler {
        public getInstalledPackages(Context context) {
            super(context);
        }

        @Override
        protected void afterInvoke(Object receiver, Method method, Object[] args, Object invokeResult) throws Throwable {
            //API 2.3
        /* public List<PackageInfo> getInstalledPackages(int flags) throws RemoteException;*/

            //API 4.01, 4.0.3_r1, 4.1.1_r1
        /*public ParceledListSlice getInstalledPackages(int flags, String lastRead) */

            //API 4.2_r1
        /* public ParceledListSlice getInstalledPackages(int flags, String lastRead, int userId) throws RemoteException*/

            //API 4.3_r1, 4.4_r1,5.0.2_r1
        /*public ParceledListSlice getInstalledPackages(int flags, int userId) throws RemoteException;*/
            try {
                if (invokeResult != null && ParceledListSliceCompat.isParceledListSlice(invokeResult)) {
                    if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2) {
                        Method getListMethod = MethodUtils.getAccessibleMethod(invokeResult.getClass(), "getList");
                        List data = (List) getListMethod.invoke(invokeResult);
                        final int index0 = 0;
                        if (args.length > index0 && args[index0] instanceof Integer) {
                            int flags = (Integer) args[index0];
                            List<PackageInfo> infos = PluginManager.getInstance().getInstalledPackages(flags);
                            if (infos != null && infos.size() > 0) {
                                data.addAll(infos);
                            }
                        }
                    } else {
                        Method isLastSliceMethod = invokeResult.getClass().getMethod("isLastSlice");
                        Method setLastSlice = invokeResult.getClass().getMethod("setLastSlice", boolean.class);
                        Method appendMethod = invokeResult.getClass().getMethod("append", Parcelable.class);
                        Method populateList = invokeResult.getClass().getMethod("populateList", List.class, Parcelable.Creator.class);
                        if (!setLastSlice.isAccessible()) {
                            setLastSlice.setAccessible(true);
                        }
                        if (!populateList.isAccessible()) {
                            populateList.setAccessible(true);
                        }
                        if (!isLastSliceMethod.isAccessible()) {
                            isLastSliceMethod.setAccessible(true);
                        }
                        if (!appendMethod.isAccessible()) {
                            appendMethod.setAccessible(true);
                        }
                        boolean isLastSlice = (Boolean) isLastSliceMethod.invoke(invokeResult);
                        if (isLastSlice) {
                            final int index0 = 0;
                            if (args.length > index0 && args[index0] instanceof Integer) {
                                int flags = (Integer) args[index0];
                                List<PackageInfo> infos = PluginManager.getInstance().getInstalledPackages(flags);
                                if (infos != null && infos.size() > 0) {
                                    final List<PackageInfo> packageInfos = new ArrayList<PackageInfo>();
                                    populateList.invoke(invokeResult, packageInfos, PackageInfo.CREATOR);
                                    packageInfos.addAll(infos);
                                    Object parceledListSlice = invokeResult.getClass().newInstance();
                                    for (PackageInfo packageInfo : packageInfos) {
                                        appendMethod.invoke(parceledListSlice, packageInfo);
                                    }
                                    setLastSlice.invoke(parceledListSlice, true);
                                    setFakedResult(parceledListSlice);
                                }
                            }
                        }
                    }
                } else if (invokeResult instanceof List) {
                    final int index0 = 0;
                    if (args.length > index0 && args[index0] instanceof Integer) {
                        int flags = (Integer) args[index0];
                        List<PackageInfo> infos = PluginManager.getInstance().getInstalledPackages(flags);
                        if (infos != null && infos.size() > 0) {
                            List old = (List) invokeResult;
                            old.addAll(infos);
                        }
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
            super.afterInvoke(receiver, method, args, invokeResult);
        }
    }

    private class getPackagesHoldingPermissions extends HookedMethodHandler {
        public getPackagesHoldingPermissions(Context context) {
            super(context);
        }

        //API 2.3, 4.01, 4.0.3_r1, 4.1.1_r1, 4.2_r1 NO
        //API 4.3_r1, 4.4_r1, 5.0.2_r1
        /* public ParceledListSlice getPackagesHoldingPermissions(String[] permissions, int flags, int userId) throws RemoteException;*/
    }

    private class getInstalledApplications extends HookedMethodHandler {
        public getInstalledApplications(Context context) {
            super(context);
        }

        @Override
        protected void afterInvoke(Object receiver, Method method, Object[] args, Object invokeResult) throws Throwable {
            //API 2.3
        /* public List<ApplicationInfo> getInstalledApplications(int flags) throws RemoteException;*/
            //API 4.01, 4.0.3_r1
        /*public ParceledListSlice getInstalledApplications(int flags, java.lang.String lastRead) throws RemoteException;*/

            //API 4.1.1_r1 , 4.2_r1
        /*  public ParceledListSlice getInstalledApplications(int flags, java.lang.String lastRead, int userId) throws RemoteException*/

            //API 4.3_r1,4.4_r1, 5.0.2_r1
        /* public ParceledListSlice getInstalledApplications(int flags, int userId) throws RemoteException */
            try {
                if (ParceledListSliceCompat.isParceledListSlice(invokeResult)) {
                    if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2) {
                        Method getListMethod = MethodUtils.getAccessibleMethod(invokeResult.getClass(), "getList");
                        List data = (List) getListMethod.invoke(invokeResult);
                        final int index0 = 0;
                        if (args.length > index0 && args[index0] instanceof Integer) {
                            int flags = (Integer) args[index0];
                            List<ApplicationInfo> infos = PluginManager.getInstance().getInstalledApplications(flags);
                            if (infos != null && infos.size() > 0) {
                                data.addAll(infos);
                            }
                        }
                    } else {
                        Method isLastSliceMethod = invokeResult.getClass().getMethod("isLastSlice");
                        Method setLastSlice = invokeResult.getClass().getMethod("setLastSlice", boolean.class);
                        Method appendMethod = invokeResult.getClass().getMethod("append", Parcelable.class);
                        Method populateList = invokeResult.getClass().getMethod("populateList", List.class, Parcelable.Creator.class);
                        if (!setLastSlice.isAccessible()) {
                            setLastSlice.setAccessible(true);
                        }
                        if (!populateList.isAccessible()) {
                            populateList.setAccessible(true);
                        }
                        if (!isLastSliceMethod.isAccessible()) {
                            isLastSliceMethod.setAccessible(true);
                        }
                        if (!appendMethod.isAccessible()) {
                            appendMethod.setAccessible(true);
                        }
                        boolean isLastSlice = (Boolean) isLastSliceMethod.invoke(invokeResult);
                        if (isLastSlice) {
                            final int index0 = 0;
                            if (args.length > index0 && args[index0] instanceof Integer) {
                                int flags = (Integer) args[index0];
                                List<ApplicationInfo> infos = PluginManager.getInstance().getInstalledApplications(flags);
                                if (infos != null && infos.size() > 0) {
                                    final List<ApplicationInfo> packageInfos = new ArrayList<ApplicationInfo>();
                                    populateList.invoke(invokeResult, packageInfos, ApplicationInfo.CREATOR);
                                    packageInfos.addAll(infos);
                                    Object parceledListSlice = invokeResult.getClass().newInstance();
                                    for (ApplicationInfo info : packageInfos) {
                                        appendMethod.invoke(parceledListSlice, info);
                                    }
                                    setLastSlice.invoke(parceledListSlice, true);
                                    setFakedResult(parceledListSlice);
                                }
                            }
                        }
                    }
                } else if (invokeResult instanceof List) {
                    final int index0 = 0;
                    if (args.length > index0 && args[index0] instanceof Integer) {
                        int flags = (Integer) args[index0];
                        List<ApplicationInfo> infos = PluginManager.getInstance().getInstalledApplications(flags);
                        if (infos != null && infos.size() > 0) {
                            List old = (List) invokeResult;
                            old.addAll(infos);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "fake getInstalledApplications", e);
            }
            super.afterInvoke(receiver, method, args, invokeResult);
        }
    }

    private class getPersistentApplications extends HookedMethodHandler {
        public getPersistentApplications(Context context) {
            super(context);
        }
        //API 2.3, 4.01, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*  public List<ApplicationInfo> getPersistentApplications(int flags) throws RemoteException;*/
    }

    private class resolveContentProvider extends HookedMethodHandler {
        public resolveContentProvider(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 2.3, 4.01, 4.0.3_r1
        /*public ProviderInfo resolveContentProvider(String name, int flags) throws RemoteException;*/
            //API 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /* public ProviderInfo resolveContentProvider(String name, int flags, int userId) throws RemoteException*/

            return super.beforeInvoke(receiver, method, args);
        }

        @Override
        protected void afterInvoke(Object receiver, Method method, Object[] args, Object invokeResult) throws Throwable {
            if (args != null) {
                if (invokeResult == null) {
                    final int index0 = 0, index1 = 1;
                    if (args.length >= 2 && args[index0] instanceof String && args[index1] instanceof Integer) {
                        String name = (String) args[index0];
                        Integer flags = (Integer) args[index1];
                        ProviderInfo info = PluginManager.getInstance().resolveContentProvider(name, flags);
                        if (info != null) {
                            setFakedResult(info);
                        }
                    }
                }
            }
            super.afterInvoke(receiver, method, args, invokeResult);
        }
    }

    private class querySyncProviders extends HookedMethodHandler {
        public querySyncProviders(Context context) {
            super(context);
        }
        //API 2.3, 4.01, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*  public void querySyncProviders(List<String> outNames, List<ProviderInfo> outInfo) throws RemoteException;*/
        //TODO 查询插件的结果并入到返回值中。
    }

    private class queryContentProviders extends HookedMethodHandler {
        public queryContentProviders(Context context) {
            super(context);
        }

        //API 2.3, 4.01, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*public List<ProviderInfo> queryContentProviders(String processName, int uid, int flags) throws RemoteException;*/
        //TODO 查询插件的结果并入到返回值中。
    }

    private class getInstrumentationInfo extends HookedMethodHandler {
        public getInstrumentationInfo(Context context) {
            super(context);
        }

        //API 2.3, 4.01, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*public InstrumentationInfo getInstrumentationInfo(ComponentName className, int flags) throws RemoteException;*/
        //FIXME 自动化测试相关的东西，先不处理。
    }

    private class queryInstrumentation extends HookedMethodHandler {
        public queryInstrumentation(Context context) {
            super(context);
        }

        //API 2.3, 4.01, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*public List<InstrumentationInfo> queryInstrumentation(String targetPackage, int flags) throws RemoteException;*/
        //FIXME 自动化测试相关的东西，先不处理。
    }

    private class getInstallerPackageName extends HookedMethodHandler {
        public getInstallerPackageName(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 2.3, 4.01, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /* public String getInstallerPackageName(String packageName) throws RemoteException;*/
            if (args != null) {
                final int index = 0;
                if (args.length > index && args[index] instanceof String) {
                    String packageName = (String) args[index];
                    if (PluginManager.getInstance().isPluginPackage(packageName)) {
                        setFakedResult(mHostContext.getPackageName());
                        return true;
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class addPackageToPreferred extends HookedMethodHandler {
        public addPackageToPreferred(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 2.3, 4.01, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*public void addPackageToPreferred(String packageName) throws RemoteException;*/
            if (args != null) {
                final int index = 0;
                if (args.length > index && args[index] instanceof String) {
                    String packageName = (String) args[index];
                    if (PluginManager.getInstance().isPluginPackage(packageName)) {
                        args[index] = mHostContext.getPackageName();
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class removePackageFromPreferred extends HookedMethodHandler {
        public removePackageFromPreferred(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 2.3, 4.01, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
         /*public void removePackageFromPreferred(String packageName) throws RemoteException;*/
            if (args != null) {
                final int index = 0;
                if (args.length > index && args[index] instanceof String) {
                    String packageName = (String) args[index];
                    if (PluginManager.getInstance().isPluginPackage(packageName)) {
                        args[index] = mHostContext.getPackageName();
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class getPreferredPackages extends HookedMethodHandler {
        public getPreferredPackages(Context context) {
            super(context);
        }
        //API 2.3, 4.01, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*public List<PackageInfo> getPreferredPackages(int flags) throws RemoteException;*/
        //这里插件没有结果的，所以就不处理了。
    }

    private class resetPreferredActivities extends HookedMethodHandler {
        public resetPreferredActivities(Context context) {
            super(context);
        }
        //API  4.3_r1, 4.4_r1, 5.0.2_r1
        /* public void resetPreferredActivities(int userId) throws android.os.RemoteException;*/
        //这里插件没有结果的，所以就不处理了。
    }

    private class getLastChosenActivity extends HookedMethodHandler {
        public getLastChosenActivity(Context context) {
            super(context);
        }
        //API 4.4_r1, 5.0.2_r1
        /*public ResolveInfo getLastChosenActivity(Intent intent, String resolvedType, int flags) throws RemoteException;*/
        //这里插件没有结果的，所以就不处理了。
    }

    private class setLastChosenActivity extends HookedMethodHandler {
        public setLastChosenActivity(Context context) {
            super(context);
        }

        //API 4.4_r1, 5.0.2_r1
        /*public void setLastChosenActivity(Intent intent, String resolvedType, int flags, IntentFilter filter, int match, ComponentName activity) throws RemoteException;*/
        //这里插件没有结果的，所以就不处理了。
    }

    private class addPreferredActivity extends HookedMethodHandler {
        public addPreferredActivity(Context context) {
            super(context);
        }

        //API 2.3, 4.01, 4.0.3_r1, 4.1.1_r1
        /* public void addPreferredActivity(IntentFilter filter, int match, ComponentName[] set, ComponentName activity) throws RemoteException;*/
        //API  4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /* public void addPreferredActivity(IntentFilter filter, int match, ComponentName[] set, ComponentName activity, int userId) throws RemoteException;*/
        //这里插件没有结果的，所以就不处理了。
    }

    private class replacePreferredActivity extends HookedMethodHandler {
        public replacePreferredActivity(Context context) {
            super(context);
        }

        //API 2.3, 4.01, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1
        /*public void replacePreferredActivity(IntentFilter filter, int match, ComponentName[] set, ComponentName activity) throws RemoteException;*/

        //API 5.0.2_r1
        /*public void replacePreferredActivity(IntentFilter filter, int match, ComponentName[] set, ComponentName activity, int userId) throws RemoteException;*/
        //这里插件没有结果的，所以就不处理了。
    }

    private class clearPackagePreferredActivities extends HookedMethodHandler {
        public clearPackagePreferredActivities(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 2.3, 4.01, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*public void clearPackagePreferredActivities(String packageName) throws RemoteException;*/
            if (args != null) {
                final int index = 0;
                if (args.length > index && args[index] instanceof String) {
                    String packageName = (String) args[index];
                    if (PluginManager.getInstance().isPluginPackage(packageName)) {
                        args[index] = mHostContext.getPackageName();
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class getPreferredActivities extends HookedMethodHandler {
        public getPreferredActivities(Context context) {
            super(context);
        }

        //API 2.3, 4.01, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /* public int getPreferredActivities(List<IntentFilter> outFilters, List<ComponentName> outActivities, String packageName) throws RemoteException;*/
        //这里插件没有结果的，所以就不处理了。
    }

    //ONLY for 4.4_r1 or later
    private class getHomeActivities extends HookedMethodHandler {
        public getHomeActivities(Context context) {
            super(context);
        }

        //API 4.4_r1, 5.0.2_r1
        /* public ComponentName getHomeActivities(List<ResolveInfo> outHomeCandidates) throws RemoteException;*/
        //这里插件没有结果的，所以就不处理了。
    }

    private class setComponentEnabledSetting extends HookedMethodHandler {
        public setComponentEnabledSetting(Context context) {
            super(context);
        }

        //API 2.3
        /* public void setComponentEnabledSetting(ComponentName componentName, int newState, int flags) throws RemoteException;*/

        //API 4.01

        //API 4.0.3_r1

        //API 4.1.1_r1

        //API 4.2_r1

        //API 4.3_r1

        //API 4.4_r1

        //API 5.0.2_r1
        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            final int index = 0;
            if (args != null && args.length > index && args[index] instanceof ComponentName) {
                ComponentName componentName = (ComponentName) args[index];
                if (PluginManager.getInstance().isPluginPackage(componentName)) {
                    setFakedResult(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
                    return true;
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class getComponentEnabledSetting extends HookedMethodHandler {
        public getComponentEnabledSetting(Context context) {
            super(context);
        }

        //API 2.3, 4.01, 4.0.3_r1, 4.1.1_r1
        /*public int getComponentEnabledSetting(ComponentName componentName) throws RemoteException;*/
        //API 4.2_r1, 4.3_r1,4.4_r1, 5.0.2_r1
        /*public void setComponentEnabledSetting(ComponentName componentName, int newState, int flags, int userId) throws RemoteException*/

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            final int index = 0;
            if (args != null && args.length > index && args[index] instanceof ComponentName) {
                ComponentName componentName = (ComponentName) args[index];
                if (PluginManager.getInstance().isPluginPackage(componentName)) {
                    setFakedResult(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
                    return true;
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class setApplicationEnabledSetting extends HookedMethodHandler {
        public setApplicationEnabledSetting(Context context) {
            super(context);
        }

        //API 2.3
        /*public void setApplicationEnabledSetting(String packageName, int newState, int flags) throws RemoteException;*/
        //API  4.01, 4.0.3_r1, 4.1.1_r1, 4.2_r1
        /*public void setApplicationEnabledSetting(String packageName, int newState, int flags, int userId) throws RemoteException*/
        //API  4.3_r1, 4.4_r1, 5.0.2_r1
        /*public void setApplicationEnabledSetting(String packageName, int newState, int flags, int userId, String callingPackage) throws RemoteException;*/
        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            final int index = 0;
            if (args != null && args.length > index && args[index] instanceof String) {
                String packageName = (String) args[index];
                if (PluginManager.getInstance().isPluginPackage(packageName)) {
                    return true;
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class getApplicationEnabledSetting extends HookedMethodHandler {
        public getApplicationEnabledSetting(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 2.3, 4.01, 4.0.3_r1,
        /* public int getApplicationEnabledSetting(String packageName) throws RemoteException;*/
            //API 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*public int getApplicationEnabledSetting(String packageName, int userId) throws RemoteException;*/
            if (args != null) {
                final int index = 0;
                if (args.length > index && args[index] instanceof String) {
                    String packageName = (String) args[index];
                    if (PluginManager.getInstance().isPluginPackage(packageName)) {
                        //DO NOTHING
                        setFakedResult(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
                        return true;
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class setPackageStoppedState extends HookedMethodHandler {
        public setPackageStoppedState(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 4.01, 4.0.3_r1
        /* public void setPackageStoppedState(String packageName, boolean stopped) throws RemoteException;*/

            //API 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*public void setPackageStoppedState(java.lang.String packageName, boolean stopped, int userId) throws android.os.RemoteException;*/
            if (args != null) {
                final int index = 0;
                if (args.length > index && args[index] instanceof String) {
                    String packageName = (String) args[index];
                    if (PluginManager.getInstance().isPluginPackage(packageName)) {
                        //DO NOTHING
                        PluginManager.getInstance().forceStopPackage(packageName);
                        return true;
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class deleteApplicationCacheFiles extends HookedMethodHandler {
        public deleteApplicationCacheFiles(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 2.3, 4.01, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /* public void deleteApplicationCacheFiles(String packageName, IPackageDataObserver observer) throws RemoteException;*/
            if (args != null) {
                final int index0 = 0, index1 = 1;
                if (args.length >= 2 && args[index0] instanceof String && IPackageDataObserverCompat.isIPackageDataObserver(args[index1])) {
                    String packageName = (String) args[index0];
                    if (PluginManager.getInstance().isPluginPackage(packageName)) {
                        final Object observer = args[index1];
                        PluginManager.getInstance().deleteApplicationCacheFiles(packageName, observer);
                        return true;
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class clearApplicationUserData extends HookedMethodHandler {
        public clearApplicationUserData(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 2.3, 4.01, 4.0.3_r1
        /* public void clearApplicationUserData(String packageName, IPackageDataObserver observer) throws RemoteException;*/
            //API 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*public void clearApplicationUserData(String packageName, IPackageDataObserver observer, int userId) throws RemoteException;*/
            if (args != null) {
                final int index0 = 0, index1 = 1;
                if (args.length >= 2 && args[index0] instanceof String && IPackageDataObserverCompat.isIPackageDataObserver(args[index1])) {
                    String packageName = (String) args[index0];
                    if (PluginManager.getInstance().isPluginPackage(packageName)) {
                        final Object observer = args[index1];
                        PluginManager.getInstance().clearApplicationUserData(packageName, observer);
                        return true;
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class getPackageSizeInfo extends HookedMethodHandler {
        public getPackageSizeInfo(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //API 2.3, 4.01, 4.0.3_r1, 4.1.1_r1
        /*public void getPackageSizeInfo(String packageName, IPackageStatsObserver observer) throws RemoteException;*/
            //API 4.2_r1 4.3_r1, 4.4_r1, 5.0.2_r1
        /*public void getPackageSizeInfo(String packageName, int userHandle, IPackageStatsObserver observer) throws RemoteException;*/
            //TODO 获取包大小。
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class performDexOpt extends HookedMethodHandler {
        public performDexOpt(Context context) {
            super(context);
        }

        //API 2.3, 4.01, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1
        /*  public boolean performDexOpt(String packageName) throws RemoteException;*/
        //API 5.0.2_r1 NO
    }

    private class movePackage extends HookedMethodHandler {
        public movePackage(Context context) {
            super(context);
        }
        //API 2.3, 4.01, 4.0.3_r1, 4.1.1_r1, 4.2_r1, 4.3_r1, 4.4_r1, 5.0.2_r1
        /*  public void movePackage(String packageName, IPackageMoveObserver observer, int flags) throws RemoteException;*/
    }
}
