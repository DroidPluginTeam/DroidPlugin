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

package com.morgoo.droidplugin.hook.binder;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;

import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.handle.IClipboardHookHandle;
import com.morgoo.droidplugin.hook.handle.IWindowManagerHookHandle;
import com.morgoo.droidplugin.reflect.FieldUtils;
import com.morgoo.helper.Log;
import com.morgoo.helper.compat.IClipboardCompat;
import com.morgoo.helper.compat.IWindowManagerCompat;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/6/17.
 */
public class IWindowManagerBinderHook extends BinderHook {

    private final static String SERVICE_NAME = "window";
    private static final String TAG = IWindowManagerBinderHook.class.getSimpleName();

    public IWindowManagerBinderHook(Context hostContext) {
        super(hostContext);
    }

    @Override
    public Object getOldObj() throws Exception {
        IBinder iBinder = MyServiceManager.getOriginService(SERVICE_NAME);
        return IWindowManagerCompat.asInterface(iBinder);
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    protected BaseHookHandle createHookHandle() {
        return new IWindowManagerHookHandle(mHostContext);
    }

    @Override
    protected void onInstall(ClassLoader classLoader) throws Throwable {
        super.onInstall(classLoader);
        try {
            Class claszz = Class.forName("com.android.internal.policy.PhoneWindow$WindowManagerHolder");
            FieldUtils.writeStaticField(claszz, "sWindowManager", MyServiceManager.getProxiedObj(getServiceName()));
        } catch (Exception e) {
            Log.w(TAG, "onInstall writeStaticField to sWindowManager fail", e);
        }
    }

    public static void fixWindowManagerHook(Activity activity) {
        try {
            Object mWindow = FieldUtils.readField(activity, "mWindow");
            Class clazz = mWindow.getClass();
            Class WindowManagerHolder = Class.forName(clazz.getName() + "$WindowManagerHolder");
            Object obj = FieldUtils.readStaticField(WindowManagerHolder, "sWindowManager");
            Object proxiedObj = MyServiceManager.getProxiedObj(SERVICE_NAME);
            if (obj != proxiedObj) {
                return;
            }
            FieldUtils.writeStaticField(WindowManagerHolder, "sWindowManager", proxiedObj);
        } catch (Exception e) {
            Log.w(TAG, "fixWindowManagerHook writeStaticField to sWindowManager fail", e);
        }

    }
}
