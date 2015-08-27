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

import android.content.Context;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;

import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.handle.IAudioServiceHookHandle;
import com.morgoo.droidplugin.hook.handle.IInputMethodManagerHookHandle;
import com.morgoo.droidplugin.reflect.FieldUtils;
import com.morgoo.helper.compat.IAudioServiceCompat;
import com.morgoo.helper.compat.IInputMethodManagerCompat;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/6/4.
 */
public class IInputMethodManagerBinderHook extends BinderHook {

    private final static String SERVICE_NAME = Context.INPUT_METHOD_SERVICE;

    public IInputMethodManagerBinderHook(Context hostContext) {
        super(hostContext);
    }

    @Override
    public Object getOldObj() throws Exception {
        IBinder iBinder = MyServiceManager.getOriginService(SERVICE_NAME);
        return IInputMethodManagerCompat.asInterface(iBinder);
    }

    @Override
    protected void onInstall(ClassLoader classLoader) throws Throwable {
        super.onInstall(classLoader);
        Object obj = FieldUtils.readStaticField(InputMethodManager.class, "sInstance");
        if (obj != null) {
            FieldUtils.writeStaticField(InputMethodManager.class, "sInstance", null);
        }
        mHostContext.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    protected BaseHookHandle createHookHandle() {
        return new IInputMethodManagerHookHandle(mHostContext);
    }
}
