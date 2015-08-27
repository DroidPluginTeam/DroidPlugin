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
import android.view.WindowManager;

import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.HookedMethodHandler;
import com.morgoo.droidplugin.hook.proxy.IContentProviderHook;
import com.morgoo.droidplugin.hook.proxy.IWindowSessionHook;
import com.morgoo.droidplugin.reflect.Utils;
import com.morgoo.helper.MyProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/6/17.
 */
public class IWindowManagerHookHandle extends BaseHookHandle {

    public IWindowManagerHookHandle(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected void init() {
        sHookedMethodHandlers.put("openSession", new openSession(mHostContext));
        sHookedMethodHandlers.put("overridePendingAppTransition", new overridePendingAppTransition(mHostContext));
        sHookedMethodHandlers.put("setAppStartingWindow", new setAppStartingWindow(mHostContext));

    }

    private class openSession extends HookedMethodHandler {
        public openSession(Context hostContext) {
            super(hostContext);
        }

        @Override
        protected void afterInvoke(Object receiver, Method method, Object[] args, Object invokeResult) throws Throwable {
            super.afterInvoke(receiver, method, args, invokeResult);
            Class clazz = invokeResult.getClass();
            IWindowSessionHook invocationHandler = new IWindowSessionHook(mHostContext, invokeResult);
            invocationHandler.setEnable(true);
            List<Class<?>> interfaces = Utils.getAllInterfaces(clazz);
            Class[] ifs = interfaces != null && interfaces.size() > 0 ? interfaces.toArray(new Class[interfaces.size()]) : new Class[0];
            Object newProxy = MyProxy.newProxyInstance(clazz.getClassLoader(), ifs, invocationHandler);
            setFakedResult(newProxy);
        }
    }

    private class overridePendingAppTransition extends HookedMethodHandler {
        public overridePendingAppTransition(Context hostContext) {
            super(hostContext);
        }
    }

    private class setAppStartingWindow extends HookedMethodHandler {
        public setAppStartingWindow(Context hostContext) {
            super(hostContext);
        }
    }
}
