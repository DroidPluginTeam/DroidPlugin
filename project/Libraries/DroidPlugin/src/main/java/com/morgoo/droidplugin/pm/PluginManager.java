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

package com.morgoo.droidplugin.pm;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.morgoo.droidplugin.BuildConfig;
import com.morgoo.droidplugin.PluginManagerService;
import com.morgoo.droidplugin.PluginServiceProvider;
import com.morgoo.droidplugin.reflect.MethodUtils;
import com.morgoo.helper.Log;
import com.morgoo.helper.compat.BundleCompat;
import com.morgoo.helper.compat.ContentProviderCompat;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 插件包管理服务的客户端实现。
 * <p/>
 * Code by Andy Zhang (zhangyong232@gmail.com) on  2015/2/11.
 */
public class PluginManager implements ServiceConnection {

    public static final String ACTION_PACKAGE_ADDED = "com.morgoo.droidplugin.PACKAGE_ADDED";
    public static final String ACTION_PACKAGE_REMOVED = "com.morgoo.droidplugin.PACKAGE_REMOVED";
    public static final String ACTION_DROIDPLUGIN_INIT = "com.morgoo.droidplugin.ACTION_DROIDPLUGIN_INIT";
    public static final String ACTION_MAINACTIVITY_ONCREATE = "com.morgoo.droidplugin.ACTION_MAINACTIVITY_ONCREATE";
    public static final String ACTION_MAINACTIVITY_ONDESTORY = "com.morgoo.droidplugin.ACTION_MAINACTIVITY_ONDESTORY";
    public static final String ACTION_SETTING = "com.morgoo.droidplugin.ACTION_SETTING";
    public static final String ACTION_SHORTCUT_PROXY = "com.morgoo.droidplugin.ACTION_SHORTCUT_PROXY";


    public static final String EXTRA_PID = "com.morgoo.droidplugin.EXTRA_PID";
    public static final String EXTRA_PACKAGENAME = "com.morgoo.droidplugin.EXTRA_EXTRA_PACKAGENAME";

    public static final String STUB_AUTHORITY_NAME = BuildConfig.AUTHORITY_NAME;
    public static final String EXTRA_APP_PERSISTENT = "com.morgoo.droidplugin.EXTRA_APP_PERSISTENT";


    public static final int INSTALL_FAILED_NO_REQUESTEDPERMISSION = -100001;
    public static final int STUB_NO_ACTIVITY_MAX_NUM = 4;


    private static final String TAG = PluginManager.class.getSimpleName();


    private Context mHostContext;
    private static PluginManager sInstance = null;

    private List<WeakReference<ServiceConnection>> sServiceConnection = Collections.synchronizedList(new ArrayList<WeakReference<ServiceConnection>>(1));

    @Override
    public void onServiceConnected(final ComponentName componentName, final IBinder iBinder) {
        mPluginManager = IPluginManager.Stub.asInterface(iBinder);
        new Thread() {
            @Override
            public void run() {
                try {
                    mPluginManager.waitForReady();
                    mPluginManager.registerApplicationCallback(new IApplicationCallback.Stub() {

                        @Override
                        public Bundle onCallback(Bundle extra) throws RemoteException {
                            return extra;
                        }
                    });

                    Iterator<WeakReference<ServiceConnection>> iterator = sServiceConnection.iterator();
                    while (iterator.hasNext()) {
                        WeakReference<ServiceConnection> wsc = iterator.next();
                        ServiceConnection sc = wsc != null ? wsc.get() : null;
                        if (sc != null) {
                            sc.onServiceConnected(componentName, iBinder);
                        } else {
                            iterator.remove();
                        }
                    }

                    mPluginManager.asBinder().linkToDeath(new IBinder.DeathRecipient() {
                        @Override
                        public void binderDied() {
                            onServiceDisconnected(componentName);
                        }
                    }, 0);

                    Log.i(TAG, "PluginManager ready!");
                } catch (Throwable e) {
                    Log.e(TAG, "Lost the mPluginManager connect...", e);
                } finally {
                    try {
                        synchronized (mWaitLock) {
                            mWaitLock.notifyAll();
                        }
                    } catch (Exception e) {
                        Log.i(TAG, "PluginManager notifyAll:" + e.getMessage());
                    }
                }
            }
        }.start();
        Log.i(TAG, "onServiceConnected connected OK!");
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Log.i(TAG, "onServiceDisconnected disconnected!");
        mPluginManager = null;

        Iterator<WeakReference<ServiceConnection>> iterator = sServiceConnection.iterator();
        while (iterator.hasNext()) {
            WeakReference<ServiceConnection> wsc = iterator.next();
            ServiceConnection sc = wsc != null ? wsc.get() : null;
            if (sc != null) {
                sc.onServiceDisconnected(componentName);
            } else {
                iterator.remove();
            }
        }
        //服务连接断开，需要重新连接。
        connectToService();
    }

    private Object mWaitLock = new Object();

    public void waitForConnected() {
        if (isConnected()) {
            return;
        } else {
            try {
                synchronized (mWaitLock) {
                    mWaitLock.wait();
                }
            } catch (InterruptedException e) {
                Log.i(TAG, "waitForConnected:" + e.getMessage());
            }
            Log.i(TAG, "waitForConnected finish");
        }
    }


    /**
     * 提供超时设置的waitForConnected版本
     *
     * @param timeout，当超时时间大于0时超时设置生效
     */
    public void waitForConnected(long timeout) {
        if (timeout > 0) {
            if (isConnected()) {
                return;
            } else {
                try {
                    synchronized (mWaitLock) {
                        mWaitLock.wait(timeout);
                    }
                } catch (InterruptedException e) {
                    Log.i(TAG, "waitForConnected:" + e.getMessage());
                }
                Log.i(TAG, "waitForConnected finish");
            }
        } else {
            waitForConnected();
        }
    }


    private IPluginManager mPluginManager;

    public void connectToService() {
        if (mPluginManager == null) {
            try {
                Intent intent = new Intent(mHostContext, PluginManagerService.class);
                intent.setPackage(mHostContext.getPackageName());
                mHostContext.startService(intent);

                String auth = mHostContext.getPackageName() + ".plugin.servicemanager";
                Uri uri = Uri.parse("content://" + auth);
                Bundle args = new Bundle();
                args.putString(PluginServiceProvider.URI_VALUE, "content://" + auth);
                Bundle res = ContentProviderCompat.call(mHostContext, uri,
                        PluginServiceProvider.Method_GetManager,
                        null, args);
                if (res != null) {
                    IBinder clientBinder = BundleCompat.getBinder(res, PluginServiceProvider.Arg_Binder);
                    onServiceConnected(intent.getComponent(), clientBinder);
                } else {
                    mHostContext.bindService(intent, this, Context.BIND_AUTO_CREATE);
                }
            } catch (Exception e) {
                Log.e(TAG, "connectToService", e);
            }

        }
    }

    public void addServiceConnection(ServiceConnection sc) {
        sServiceConnection.add(new WeakReference<ServiceConnection>(sc));
    }

    public void removeServiceConnection(ServiceConnection sc) {
        Iterator<WeakReference<ServiceConnection>> iterator = sServiceConnection.iterator();
        while (iterator.hasNext()) {
            WeakReference<ServiceConnection> wsc = iterator.next();
            if (wsc.get() == sc) {
                iterator.remove();
            }
        }
    }


    public void init(Context hostContext) {
        mHostContext = hostContext;
        connectToService();
    }

    public Context getHostContext() {
        return mHostContext;
    }

    public boolean isConnected() {
        return mHostContext != null && mPluginManager != null;
    }

    public static PluginManager getInstance() {
        if (sInstance == null) {
            sInstance = new PluginManager();
        }
        return sInstance;
    }

    //////////////////////////
    //  API
    //////////////////////////
    public PackageInfo getPackageInfo(String packageName, int flags) throws RemoteException {
        try {
            if (mPluginManager != null) {
                return mPluginManager.getPackageInfo(packageName, flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "getPackageInfo", e);
        }
        return null;
    }


    public boolean isPluginPackage(String packageName) throws RemoteException {
        try {
            if (mHostContext == null) {
                return false;
            }
            if (TextUtils.equals(mHostContext.getPackageName(), packageName)) {
                return false;
            }

            if (mPluginManager != null && packageName != null) {
                return mPluginManager.isPluginPackage(packageName);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "isPluginPackage", e);
        }
        return false;
    }

    public boolean isPluginPackage(ComponentName className) throws RemoteException {
        if (className == null) {
            return false;
        }
        return isPluginPackage(className.getPackageName());
    }

    public ActivityInfo getActivityInfo(ComponentName className, int flags) throws NameNotFoundException, RemoteException {

        try {
            if (className == null) {
                return null;
            }
            if (mPluginManager != null && className != null) {
                return mPluginManager.getActivityInfo(className, flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            Log.e(TAG, "getActivityInfo RemoteException", e);
        } catch (Exception e) {
            Log.e(TAG, "getActivityInfo", e);
        }
        return null;
    }

    public ActivityInfo getReceiverInfo(ComponentName className, int flags) throws NameNotFoundException, RemoteException {
        if (className == null) {
            return null;
        }
        try {
            if (mPluginManager != null && className != null) {
                return mPluginManager.getReceiverInfo(className, flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "getReceiverInfo", e);
        }
        return null;
    }

    public ServiceInfo getServiceInfo(ComponentName className, int flags) throws NameNotFoundException, RemoteException {
        if (className == null) {
            return null;
        }
        try {
            if (mPluginManager != null && className != null) {
                return mPluginManager.getServiceInfo(className, flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "getServiceInfo", e);
        }
        return null;
    }

    public ProviderInfo getProviderInfo(ComponentName className, int flags) throws NameNotFoundException, RemoteException {
        if (className == null) {
            return null;
        }
        try {
            if (mPluginManager != null && className != null) {
                return mPluginManager.getProviderInfo(className, flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "getProviderInfo", e);
        }
        return null;
    }

    public ResolveInfo resolveIntent(Intent intent, String resolvedType, int flags) throws RemoteException {
        try {
            if (mPluginManager != null && intent != null) {
                return mPluginManager.resolveIntent(intent, resolvedType, flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "resolveIntent", e);
        }
        return null;
    }

    public ResolveInfo resolveService(Intent intent, String resolvedType, Integer flags) throws RemoteException {
        try {
            if (mPluginManager != null && intent != null) {
                return mPluginManager.resolveService(intent, resolvedType, flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "resolveService", e);
        }
        return null;
    }

    public List<ResolveInfo> queryIntentActivities(Intent intent, String resolvedType, int flags) throws RemoteException {
        try {
            if (mPluginManager != null && intent != null) {
                return mPluginManager.queryIntentActivities(intent, resolvedType, flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            Log.e(TAG, "queryIntentActivities RemoteException", e);
        } catch (Exception e) {
            Log.e(TAG, "queryIntentActivities", e);
        }
        return null;
    }

    public List<ResolveInfo> queryIntentReceivers(Intent intent, String resolvedType, int flags) throws RemoteException {
        try {
            if (mPluginManager != null && intent != null) {
                return mPluginManager.queryIntentReceivers(intent, resolvedType, flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "queryIntentReceivers", e);
        }
        return null;
    }

    public List<ResolveInfo> queryIntentServices(Intent intent, String resolvedType, int flags) throws RemoteException {
        try {
            if (mPluginManager != null && intent != null) {
                return mPluginManager.queryIntentServices(intent, resolvedType, flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            Log.e(TAG, "queryIntentServices RemoteException", e);
        } catch (Exception e) {
            Log.e(TAG, "queryIntentServices", e);
        }
        return null;
    }

    public List<ResolveInfo> queryIntentContentProviders(Intent intent, String resolvedType, int flags) throws RemoteException {
        try {
            if (mPluginManager != null && intent != null) {
                return mPluginManager.queryIntentContentProviders(intent, resolvedType, flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "queryIntentContentProviders", e);
        }
        return null;
    }

    public List<PackageInfo> getInstalledPackages(int flags) throws RemoteException {
        try {
            if (mPluginManager != null) {
                return mPluginManager.getInstalledPackages(flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            Log.e(TAG, "getInstalledPackages RemoteException", e);
        } catch (Exception e) {
            Log.e(TAG, "getInstalledPackages", e);
        }
        return null;
    }

    public List<ApplicationInfo> getInstalledApplications(int flags) throws RemoteException {

        try {
            if (mPluginManager != null) {
                return mPluginManager.getInstalledApplications(flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "getInstalledApplications", e);
        }
        return null;
    }

    public PermissionInfo getPermissionInfo(String name, int flags) throws RemoteException {
        try {
            if (mPluginManager != null && name != null) {
                return mPluginManager.getPermissionInfo(name, flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "getPermissionInfo", e);
        }
        return null;
    }

    public List<PermissionInfo> queryPermissionsByGroup(String group, int flags) throws RemoteException {
        try {
            if (mPluginManager != null && group != null) {
                return mPluginManager.queryPermissionsByGroup(group, flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "queryPermissionsByGroup", e);
        }
        return null;
    }

    public PermissionGroupInfo getPermissionGroupInfo(String name, int flags) throws RemoteException {
        try {
            if (mPluginManager != null && name != null) {
                return mPluginManager.getPermissionGroupInfo(name, flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "getPermissionGroupInfo", e);
        }
        return null;
    }

    public List<PermissionGroupInfo> getAllPermissionGroups(int flags) throws RemoteException {
        try {
            if (mPluginManager != null) {
                return mPluginManager.getAllPermissionGroups(flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "getAllPermissionGroups", e);
        }
        return null;
    }

    public ProviderInfo resolveContentProvider(String name, Integer flags) throws RemoteException {
        try {
            if (mPluginManager != null && name != null) {
                return mPluginManager.resolveContentProvider(name, flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "resolveContentProvider", e);
        }
        return null;
    }

    public void deleteApplicationCacheFiles(String packageName, final Object observer/*android.content.pm.IPackageDataObserver*/) throws RemoteException {
        try {
            if (mPluginManager != null && packageName != null) {
                mPluginManager.deleteApplicationCacheFiles(packageName, new com.morgoo.droidplugin.pm.IPackageDataObserver.Stub() {

                    @Override
                    public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                        if (observer != null) {
                            try {
                                MethodUtils.invokeMethod(observer, "onRemoveCompleted", packageName, succeeded);
                            } catch (Exception e) {
                                RemoteException exception = new RemoteException();
                                exception.initCause(exception);
                                throw exception;
                            }
                        }
                    }
                });
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "deleteApplicationCacheFiles", e);
        }
    }

    public void clearApplicationUserData(String packageName, final Object observer/*android.content.pm.IPackageDataObserver*/) throws RemoteException {
        try {
            if (mPluginManager != null && packageName != null) {
                mPluginManager.clearApplicationUserData(packageName, new com.morgoo.droidplugin.pm.IPackageDataObserver.Stub() {

                    @Override
                    public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                        if (observer != null) {
                            try {
                                MethodUtils.invokeMethod(observer, "onRemoveCompleted", packageName, succeeded);
                            } catch (Exception e) {
                                RemoteException exception = new RemoteException();
                                exception.initCause(exception);
                                throw exception;
                            }
                        }
                    }
                });
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "clearApplicationUserData", e);
        }
    }

    public ApplicationInfo getApplicationInfo(String packageName, int flags) throws RemoteException {
        try {
            if (mPluginManager != null && packageName != null) {
                return mPluginManager.getApplicationInfo(packageName, flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            Log.e(TAG, "getApplicationInfo RemoteException", e);
        } catch (Exception e) {
            Log.e(TAG, "getApplicationInfo", e);
        }
        return null;
    }

    public ActivityInfo selectStubActivityInfo(ActivityInfo pluginInfo) throws RemoteException {
        try {
            if (mPluginManager != null) {
                return mPluginManager.selectStubActivityInfo(pluginInfo);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "selectStubActivityInfo", e);
        }
        return null;
    }

    public ActivityInfo selectStubActivityInfo(Intent pluginInfo) throws RemoteException {
        try {
            if (mPluginManager != null) {
                return mPluginManager.selectStubActivityInfoByIntent(pluginInfo);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "selectStubActivityInfo", e);
        }
        return null;
    }

    public ServiceInfo selectStubServiceInfo(ServiceInfo pluginInfo) throws RemoteException {
        try {
            if (mPluginManager != null) {
                return mPluginManager.selectStubServiceInfo(pluginInfo);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "selectStubServiceInfo", e);
        }
        return null;
    }

    public ServiceInfo selectStubServiceInfo(Intent pluginInfo) throws RemoteException {
        try {
            if (mPluginManager != null) {
                return mPluginManager.selectStubServiceInfoByIntent(pluginInfo);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "selectStubServiceInfo", e);
        }
        return null;
    }

    public ProviderInfo selectStubProviderInfo(String name) throws RemoteException {
        try {
            if (mPluginManager != null) {
                return mPluginManager.selectStubProviderInfo(name);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "selectStubProviderInfo", e);
        }
        return null;
    }

    public ActivityInfo resolveActivityInfo(Intent intent, int flags) throws RemoteException {
        try {
            if (mPluginManager != null) {
                if (intent.getComponent() != null) {
                    return mPluginManager.getActivityInfo(intent.getComponent(), flags);
                } else {
                    ResolveInfo resolveInfo = mPluginManager.resolveIntent(intent, intent.resolveTypeIfNeeded(mHostContext.getContentResolver()), flags);
                    if (resolveInfo != null && resolveInfo.activityInfo != null) {
                        return resolveInfo.activityInfo;
                    }
                }
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
            return null;
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "selectStubActivityInfo", e);
        }
        return null;
    }

    public ServiceInfo resolveServiceInfo(Intent intent, int flags) throws RemoteException {
        try {
            if (mPluginManager != null) {
                if (intent.getComponent() != null) {
                    return mPluginManager.getServiceInfo(intent.getComponent(), flags);
                } else {
                    ResolveInfo resolveInfo = mPluginManager.resolveIntent(intent, intent.resolveTypeIfNeeded(mHostContext.getContentResolver()), flags);
                    if (resolveInfo != null && resolveInfo.serviceInfo != null) {
                        return resolveInfo.serviceInfo;
                    }
                }
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
            return null;
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "resolveServiceInfo", e);
        }
        return null;
    }

    public void killBackgroundProcesses(String packageName) throws RemoteException {
        try {
            if (mPluginManager != null) {
                mPluginManager.killBackgroundProcesses(packageName);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "killBackgroundProcesses", e);
        }
    }

    public void forceStopPackage(String packageName) throws RemoteException {
        try {
            if (mPluginManager != null) {
                mPluginManager.forceStopPackage(packageName);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "forceStopPackage", e);
        }

    }

    public boolean killApplicationProcess(String packageName) throws RemoteException {
        try {
            if (mPluginManager != null) {
                return mPluginManager.killApplicationProcess(packageName);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "killApplicationProcess", e);
        }
        return false;
    }

    public List<ActivityInfo> getReceivers(String packageName, int flags) throws RemoteException {
        try {
            if (mPluginManager != null) {
                return mPluginManager.getReceivers(packageName, flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "getReceivers", e);
        }
        return null;
    }

    public List<IntentFilter> getReceiverIntentFilter(ActivityInfo info) throws RemoteException {
        try {
            if (mPluginManager != null) {
                return mPluginManager.getReceiverIntentFilter(info);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "getReceiverIntentFilter", e);
        }
        return null;
    }

    public ServiceInfo getTargetServiceInfo(ServiceInfo info) throws RemoteException {
        try {
            if (mPluginManager != null) {
                return mPluginManager.getTargetServiceInfo(info);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "getTargetServiceInfo", e);
        }
        return null;
    }

    public int installPackage(String filepath, int flags) throws RemoteException {
        try {
            if (mPluginManager != null) {
                int result = mPluginManager.installPackage(filepath, flags);
                Log.w(TAG, String.format("%s install result %d", filepath, result));
                return result;
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "forceStopPackage", e);
        }
        return -1;
    }

    public List<String> getPackageNameByPid(int pid) throws RemoteException {
        try {
            if (mPluginManager != null) {
                return mPluginManager.getPackageNameByPid(pid);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "forceStopPackage", e);
        }
        return null;
    }


    public String getProcessNameByPid(int pid) throws RemoteException {
        try {
            if (mPluginManager != null) {
                return mPluginManager.getProcessNameByPid(pid);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "forceStopPackage", e);
        }
        return null;
    }

    public void onActivityCreated(ActivityInfo stubInfo, ActivityInfo targetInfo) throws RemoteException {
        try {
            if (mPluginManager != null) {
                mPluginManager.onActivityCreated(stubInfo, targetInfo);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "onActivityCreated", e);
        }
    }

    public void onActivityDestory(ActivityInfo stubInfo, ActivityInfo targetInfo) throws RemoteException {
        try {
            if (mPluginManager != null) {
                mPluginManager.onActivityDestory(stubInfo, targetInfo);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "onActivityDestroy", e);
        }
    }

    public void onServiceCreated(ServiceInfo stubInfo, ServiceInfo targetInfo) throws RemoteException {
        try {
            if (mPluginManager != null) {
                mPluginManager.onServiceCreated(stubInfo, targetInfo);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "onServiceCreated", e);
        }
    }


    public void onServiceDestory(ServiceInfo stubInfo, ServiceInfo targetInfo) {
        try {
            if (mPluginManager != null) {
                mPluginManager.onServiceDestory(stubInfo, targetInfo);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (Exception e) {
            Log.e(TAG, "onServiceDestroy", e);
        }
    }

    public void onProviderCreated(ProviderInfo stubInfo, ProviderInfo targetInfo) throws RemoteException {
        try {
            if (mPluginManager != null) {
                mPluginManager.onProviderCreated(stubInfo, targetInfo);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "onProviderCreated", e);
        }
    }

    public void reportMyProcessName(String stubProcessName, String targetProcessName, String targetPkg) throws RemoteException {
        try {
            if (mPluginManager != null) {
                mPluginManager.reportMyProcessName(stubProcessName, targetProcessName, targetPkg);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "reportMyProcessName", e);
        }
    }

    public void deletePackage(String packageName, int flags) throws RemoteException {
        try {
            if (mPluginManager != null) {
                mPluginManager.deletePackage(packageName, flags);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "deletePackage", e);
        }
    }


    public int checkSignatures(String pkg0, String pkg1) throws RemoteException {
        try {
            if (mPluginManager != null) {
                return mPluginManager.checkSignatures(pkg0, pkg1);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
                return PackageManager.SIGNATURE_NO_MATCH;
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "deletePackage", e);
            return PackageManager.SIGNATURE_NO_MATCH;
        }
    }

    public void onActivtyOnNewIntent(ActivityInfo stubInfo, ActivityInfo targetInfo, Intent intent) throws RemoteException {
        try {
            if (mPluginManager != null) {
                mPluginManager.onActivtyOnNewIntent(stubInfo, targetInfo, intent);
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "onActivityOnNewIntent", e);
        }
    }

    public int getMyPid() throws RemoteException {
        try {
            if (mPluginManager != null) {
                return mPluginManager.getMyPid();
            } else {
                Log.w(TAG, "Plugin Package Manager Service not be connect");
                return -1;
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "getMyPid", e);
            return -1;
        }
    }
}
