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

package com.morgoo.droidplugin.stub;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.IBinder;

import com.morgoo.droidplugin.core.Env;
import com.morgoo.droidplugin.core.PluginProcessManager;
import com.morgoo.droidplugin.pm.PluginManager;
import com.morgoo.droidplugin.reflect.FieldUtils;
import com.morgoo.droidplugin.reflect.MethodUtils;
import com.morgoo.helper.compat.ActivityThreadCompat;
import com.morgoo.helper.compat.CompatibilityInfoCompat;
import com.morgoo.helper.compat.QueuedWorkCompat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/2/9.
 */
public class ServcesManager {

    private Map<Object, Service> mTokenServices = new HashMap<Object, Service>();
    private Map<String, Service> mNameService = new HashMap<String, Service>();
    private Map<Object, Integer> mServiceTaskIds = new HashMap<Object, Integer>();

    private ServcesManager() {
    }

    private static ServcesManager sServcesManager;

    public static ServcesManager getDefault() {
        synchronized (ServcesManager.class) {
            if (sServcesManager == null) {
                sServcesManager = new ServcesManager();
            }
        }
        return sServcesManager;
    }

    public boolean hasServiceRunning() {
        return mTokenServices.size() > 0 && mNameService.size() > 0;
    }

    private Object findTokenByService(Service service) {
        for (Object s : mTokenServices.keySet()) {
            if (mTokenServices.get(s) == service) {
                return s;
            }
        }
        return null;
    }

    private ClassLoader getClassLoader(ApplicationInfo pluginApplicationInfo) throws Exception {
        Object object = ActivityThreadCompat.currentActivityThread();
        if (object != null) {
            final Object obj;
            if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
                obj = MethodUtils.invokeMethod(object, "getPackageInfoNoCheck", pluginApplicationInfo, CompatibilityInfoCompat.DEFAULT_COMPATIBILITY_INFO());
            } else {
                obj = MethodUtils.invokeMethod(object, "getPackageInfoNoCheck", pluginApplicationInfo);
            }
                /*添加ClassLoader LoadedApk.mClassLoader*/
            return (ClassLoader) MethodUtils.invokeMethod(obj, "getClassLoader");
        }
        return null;
    }

    //这个需要适配,目前只是适配android api 21
    private void handleCreateServiceOne(Context hostContext, Intent stubIntent, ServiceInfo info) throws Exception {
        //            CreateServiceData data = new CreateServiceData();
        //            data.token = fakeToken;// IBinder
        //            data.info =; //ServiceInfo
        //            data.compatInfo =; //CompatibilityInfo
        //            data.intent =; //Intent
        //            activityThread.handleCreateServiceOne(data);
        //            service = activityThread.mTokenServices.get(fakeToken);
        //            activityThread.mTokenServices.remove(fakeToken);
        ResolveInfo resolveInfo = hostContext.getPackageManager().resolveService(stubIntent, 0);
        ServiceInfo stubInfo = resolveInfo != null ? resolveInfo.serviceInfo : null;
        PluginManager.getInstance().reportMyProcessName(stubInfo.processName, info.processName, info.packageName);
        PluginProcessManager.preLoadApk(hostContext, info);
        Object activityThread = ActivityThreadCompat.currentActivityThread();
        IBinder fakeToken = new MyFakeIBinder();
        Class CreateServiceData = Class.forName(ActivityThreadCompat.activityThreadClass().getName() + "$CreateServiceData");
        Constructor init = CreateServiceData.getDeclaredConstructor();
        if (!init.isAccessible()) {
            init.setAccessible(true);
        }
        Object data = init.newInstance();

        FieldUtils.writeField(data, "token", fakeToken);
        FieldUtils.writeField(data, "info", info);
        if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            FieldUtils.writeField(data, "compatInfo", CompatibilityInfoCompat.DEFAULT_COMPATIBILITY_INFO());
        }

        Method method = activityThread.getClass().getDeclaredMethod("handleCreateService", CreateServiceData);
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
        method.invoke(activityThread, data);
        Object mService = FieldUtils.readField(activityThread, "mServices");
        Service service = (Service) MethodUtils.invokeMethod(mService, "get", fakeToken);
        MethodUtils.invokeMethod(mService, "remove", fakeToken);
        mTokenServices.put(fakeToken, service);
        mNameService.put(info.name, service);


        if (stubInfo != null) {
            PluginManager.getInstance().onServiceCreated(stubInfo, info);
        }
    }

    private void handleOnStartOne(Intent intent, int flags, int startIds) throws Exception {
        ServiceInfo info = PluginManager.getInstance().resolveServiceInfo(intent, 0);
        if (info != null) {
            Service service = mNameService.get(info.name);
            if (service != null) {
                ClassLoader classLoader = getClassLoader(info.applicationInfo);
                intent.setExtrasClassLoader(classLoader);
                Object token = findTokenByService(service);
                Integer integer = mServiceTaskIds.get(token);
                if (integer == null) {
                    integer = -1;
                }
                int startId = integer + 1;
                mServiceTaskIds.put(token, startId);
                int res = service.onStartCommand(intent, flags, startId);
                QueuedWorkCompat.waitToFinish();
            }
        }
    }

    private void handleOnTaskRemovedOne(Intent intent) throws Exception {
        if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
            ServiceInfo info = PluginManager.getInstance().resolveServiceInfo(intent, 0);
            if (info != null) {
                Service service = mNameService.get(info.name);
                if (service != null) {
                    ClassLoader classLoader = getClassLoader(info.applicationInfo);
                    intent.setExtrasClassLoader(classLoader);
                    service.onTaskRemoved(intent);
                    QueuedWorkCompat.waitToFinish();
                }
                QueuedWorkCompat.waitToFinish();
            }
        }
    }


    private void handleOnDestroyOne(ServiceInfo targetInfo) {
        Service service = mNameService.get(targetInfo.name);
        if (service != null) {
            service.onDestroy();
            mNameService.remove(targetInfo.name);
            Object token = findTokenByService(service);
            mTokenServices.remove(token);
            mServiceTaskIds.remove(token);
            service = null;
            QueuedWorkCompat.waitToFinish();
            PluginManager.getInstance().onServiceDestory(null, targetInfo);
        }
        QueuedWorkCompat.waitToFinish();
    }


    private IBinder handleOnBindOne(Intent intent) throws Exception {
        ServiceInfo info = PluginManager.getInstance().resolveServiceInfo(intent, 0);
        if (info != null) {
            Service service = mNameService.get(info.name);
            if (service != null) {
                ClassLoader classLoader = getClassLoader(info.applicationInfo);
                intent.setExtrasClassLoader(classLoader);
                return service.onBind(intent);
            }
        }
        return null;
    }

    private void handleOnRebindOne(Intent intent) throws Exception {
        ServiceInfo info = PluginManager.getInstance().resolveServiceInfo(intent, 0);
        if (info != null) {
            Service service = mNameService.get(info.name);
            if (service != null) {
                ClassLoader classLoader = getClassLoader(info.applicationInfo);
                intent.setExtrasClassLoader(classLoader);
                service.onRebind(intent);
            }
        }
    }

    private boolean handleOnUnbindOne(Intent intent) throws Exception {
        ServiceInfo info = PluginManager.getInstance().resolveServiceInfo(intent, 0);
        if (info != null) {
            Service service = mNameService.get(info.name);
            if (service != null) {
                ClassLoader classLoader = getClassLoader(info.applicationInfo);
                intent.setExtrasClassLoader(classLoader);
                return service.onUnbind(intent);
            }
        }
        return false;
    }


    public int onStart(Context context, Intent intent, int flags, int startId) throws Exception {
        Intent targetIntent = intent.getParcelableExtra(Env.EXTRA_TARGET_INTENT);
        if (targetIntent != null) {
            ServiceInfo targetInfo = PluginManager.getInstance().resolveServiceInfo(targetIntent, 0);
            if (targetInfo != null) {
                Service service = mNameService.get(targetInfo.name);
                if (service == null) {

                    handleCreateServiceOne(context, intent, targetInfo);
                }
                handleOnStartOne(targetIntent, flags, startId);
            }
        }
        return -1;
    }

    public void onTaskRemoved(Context context, Intent intent) throws Exception {
        Intent targetIntent = intent.getParcelableExtra(Env.EXTRA_TARGET_INTENT);
        if (targetIntent != null) {
            ServiceInfo info = PluginManager.getInstance().resolveServiceInfo(targetIntent, 0);
            Service service = mNameService.get(info.name);
            if (service == null) {
                handleCreateServiceOne(context, intent, info);
            }
            handleOnTaskRemovedOne(targetIntent);
        }
    }

    public IBinder onBind(Context context, Intent intent) throws Exception {
        Intent targetIntent = intent.getParcelableExtra(Env.EXTRA_TARGET_INTENT);
        if (targetIntent != null) {
            ServiceInfo info = PluginManager.getInstance().resolveServiceInfo(targetIntent, 0);
            Service service = mNameService.get(info.name);
            if (service == null) {
                handleCreateServiceOne(context, intent, info);
            }
            return handleOnBindOne(targetIntent);
        }
        return null;
    }

    public void onRebind(Context context, Intent intent) throws Exception {
        Intent targetIntent = intent.getParcelableExtra(Env.EXTRA_TARGET_INTENT);
        if (targetIntent != null) {
            ServiceInfo info = PluginManager.getInstance().resolveServiceInfo(targetIntent, 0);
            Service service = mNameService.get(info.name);
            if (service == null) {
                handleCreateServiceOne(context, intent, info);
            }
            handleOnRebindOne(targetIntent);
        }
    }

    public boolean onUnbind(Intent intent) throws Exception {
        Intent targetIntent = intent.getParcelableExtra(Env.EXTRA_TARGET_INTENT);
        if (targetIntent != null) {
            ServiceInfo info = PluginManager.getInstance().resolveServiceInfo(targetIntent, 0);
            Service service = mNameService.get(info.name);
            if (service != null) {
                return handleOnUnbindOne(targetIntent);
            }
        }
        return false;
    }


    public int stopService(Context context, Intent intent) throws Exception {
        ServiceInfo targetInfo = PluginManager.getInstance().resolveServiceInfo(intent, 0);
        if (targetInfo != null) {
            handleOnUnbindOne(intent);
            handleOnDestroyOne(targetInfo);
            return 1;
        }
        return 0;
    }

    public boolean stopServiceToken(ComponentName cn, IBinder token, int startId) throws Exception {
        Service service = mTokenServices.get(token);
        if (service != null) {
            Integer lastId = mServiceTaskIds.get(token);
            if (lastId == null) {
                return false;
            }
            if (startId != lastId) {
                return false;
            }
            Intent intent = new Intent();
            intent.setComponent(cn);
            ServiceInfo info = PluginManager.getInstance().resolveServiceInfo(intent, 0);
            if (info != null) {
                handleOnUnbindOne(intent);
                handleOnDestroyOne(info);
                return true;
            }
        }
        return false;
    }

    public void onDestroy() {
        for (Service service : mTokenServices.values()) {
            service.onDestroy();
        }
        mTokenServices.clear();
        mServiceTaskIds.clear();
        mNameService.clear();
        QueuedWorkCompat.waitToFinish();
    }
}
