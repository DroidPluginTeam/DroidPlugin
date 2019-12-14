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

import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.HookedMethodHandler;
import com.morgoo.droidplugin.hook.proxy.ProxyHook;
import com.morgoo.droidplugin.reflect.FieldUtils;
import com.morgoo.droidplugin.reflect.MethodUtils;
import com.morgoo.droidplugin.reflect.Utils;
import com.morgoo.helper.Log;
import com.morgoo.helper.MyProxy;
import com.morgoo.helper.compat.ServiceManagerCompat;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/6/23.
 */
public class ServiceManagerBinderHook extends ProxyHook implements InvocationHandler {

    public ServiceManagerBinderHook(Context hostContext) {
        super(hostContext);
        setEnable(true);
    }


    @Override
    protected void onInstall(ClassLoader classLoader) throws Throwable {
        Object sServiceManager = FieldUtils.readStaticField(ServiceManagerCompat.Class(), "sServiceManager");
        if (sServiceManager == null) {
            MethodUtils.invokeStaticMethod(ServiceManagerCompat.Class(), "getIServiceManager");
            sServiceManager = FieldUtils.readStaticField(ServiceManagerCompat.Class(), "sServiceManager");
        }
        setOldObj(sServiceManager);

        Class<?> clazz = mOldObj.getClass();
        List<Class<?>> interfaces = Utils.getAllInterfaces(clazz);
        Class[] ifs = interfaces != null && interfaces.size() > 0 ? interfaces.toArray(new Class[interfaces.size()]) : new Class[0];
        Object proxiedObj = MyProxy.newProxyInstance(clazz.getClassLoader(), ifs, this);
        FieldUtils.writeStaticField(ServiceManagerCompat.Class(), "sServiceManager", proxiedObj);
    }

    private class ServiceManagerHookHandle extends BaseHookHandle {

        private ServiceManagerHookHandle(Context context) {
            super(context);
        }

        @Override
        protected void init() {
            sHookedMethodHandlers.put("getService", new getService(mHostContext));
            sHookedMethodHandlers.put("checkService", new checkService(mHostContext));
        }

        private class ServiceManagerHook extends HookedMethodHandler {
            public ServiceManagerHook(Context hostContext) {
                super(hostContext);
            }

            @Override
            protected void afterInvoke(Object receiver, Method method, Object[] args, Object invokeResult) throws Throwable {
                int index = 0;
                if (args != null && args.length > index && args[index] instanceof String) {
                    String servicename = ((String) args[index]);
                    Object proxiedObj = MyServiceManager.getProxiedObj(servicename);
                    if (proxiedObj != null) {
                        setFakedResult(proxiedObj);
                    }
                }
                Log.e("ServiceManagerBinderHook", "%s(%s)=%s", method.getName(), Arrays.toString(args), invokeResult);
                super.afterInvoke(receiver, method, args, invokeResult);
            }
        }

        private class getService extends ServiceManagerHook {
            public getService(Context hostContext) {
                super(hostContext);
            }
        }

        private class checkService extends ServiceManagerHook {
            public checkService(Context hostContext) {
                super(hostContext);
            }
        }
    }

    @Override
    protected BaseHookHandle createHookHandle() {
        return new ServiceManagerHookHandle(mHostContext);
    }


}
