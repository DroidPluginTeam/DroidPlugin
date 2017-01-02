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
import android.text.TextUtils;

import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.Hook;
import com.morgoo.droidplugin.hook.HookedMethodHandler;
import com.morgoo.droidplugin.reflect.FieldUtils;
import com.morgoo.droidplugin.reflect.Utils;
import com.morgoo.helper.MyProxy;
import com.morgoo.helper.compat.ServiceManagerCompat;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/3/2.
 */
public class ServiceManagerCacheBinderHook extends Hook implements InvocationHandler {


    private String mServiceName;

    public ServiceManagerCacheBinderHook(Context hostContext, String servicename) {
        super(hostContext);
        mServiceName = servicename;
        setEnable(true);
    }


    @Override
    protected void onInstall(ClassLoader classLoader) throws Throwable {
        Object sCacheObj = FieldUtils.readStaticField(ServiceManagerCompat.Class(), "sCache");
        if (sCacheObj instanceof Map) {
            Map sCache = (Map) sCacheObj;
            Object Obj = sCache.get(mServiceName);
            if (Obj != null && false) {
                //FIXME 已经有了怎么处理？这里我们只是把原来的给remove掉，再添加自己的。程序下次取用的时候就变成我们hook过的了。
                //但是这样有缺陷。
                throw new RuntimeException("Can not install binder hook for " + mServiceName);
            } else {
                sCache.remove(mServiceName);
                IBinder mServiceIBinder = ServiceManagerCompat.getService(mServiceName);
                if (mServiceIBinder == null) {
                    if (Obj != null && Obj instanceof IBinder && !Proxy.isProxyClass(Obj.getClass())) {
                        mServiceIBinder = ((IBinder) Obj);
                    }
                }
                if (mServiceIBinder != null) {
                    MyServiceManager.addOriginService(mServiceName, mServiceIBinder);
                    Class clazz = mServiceIBinder.getClass();
                    List<Class<?>> interfaces = Utils.getAllInterfaces(clazz);
                    Class[] ifs = interfaces != null && interfaces.size() > 0 ? interfaces.toArray(new Class[interfaces.size()]) : new Class[0];
                    IBinder mProxyServiceIBinder = (IBinder) MyProxy.newProxyInstance(clazz.getClassLoader(), ifs, this);
                    sCache.put(mServiceName, mProxyServiceIBinder);
                    MyServiceManager.addProxiedServiceCache(mServiceName, mProxyServiceIBinder);
                }
            }
        }
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            IBinder originService = MyServiceManager.getOriginService(mServiceName);
            if (!isEnable()) {
                return method.invoke(originService, args);
            }
            HookedMethodHandler hookedMethodHandler = mHookHandles.getHookedMethodHandler(method);
            if (hookedMethodHandler != null) {
                return hookedMethodHandler.doHookInner(originService, method, args);
            } else {
                return method.invoke(originService, args);
            }
        } catch (InvocationTargetException e) {
            Throwable cause = e.getTargetException();
            if (cause != null && MyProxy.isMethodDeclaredThrowable(method, cause)) {
                throw cause;
            } else if (cause != null) {
                RuntimeException runtimeException = !TextUtils.isEmpty(cause.getMessage()) ? new RuntimeException(cause.getMessage()) : new RuntimeException();
                runtimeException.initCause(cause);
                throw runtimeException;
            } else {
                RuntimeException runtimeException = !TextUtils.isEmpty(e.getMessage()) ? new RuntimeException(e.getMessage()) : new RuntimeException();
                runtimeException.initCause(e);
                throw runtimeException;
            }
        } catch (IllegalArgumentException e) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append(" DROIDPLUGIN{");
                if (method != null) {
                    sb.append("method[").append(method.toString()).append("]");
                } else {
                    sb.append("method[").append("NULL").append("]");
                }
                if (args != null) {
                    sb.append("args[").append(Arrays.toString(args)).append("]");
                } else {
                    sb.append("args[").append("NULL").append("]");
                }
                sb.append("}");

                String message = e.getMessage() + sb.toString();
                throw new IllegalArgumentException(message, e);
            } catch (Throwable e1) {
                throw e;
            }
        } catch (Throwable e) {
            if (MyProxy.isMethodDeclaredThrowable(method, e)) {
                throw e;
            } else {
                RuntimeException runtimeException = !TextUtils.isEmpty(e.getMessage()) ? new RuntimeException(e.getMessage()) : new RuntimeException();
                runtimeException.initCause(e);
                throw runtimeException;
            }
        }
    }

    private class ServiceManagerHookHandle extends BaseHookHandle {

        private ServiceManagerHookHandle(Context context) {
            super(context);
        }

        @Override
        protected void init() {
            sHookedMethodHandlers.put("queryLocalInterface", new queryLocalInterface(mHostContext));
        }


        class queryLocalInterface extends HookedMethodHandler {
            public queryLocalInterface(Context context) {
                super(context);
            }

            @Override
            protected void afterInvoke(Object receiver, Method method, Object[] args, Object invokeResult) throws Throwable {
                Object localInterface = invokeResult;
                Object proxiedObj = MyServiceManager.getProxiedObj(mServiceName);
                if (localInterface == null && proxiedObj != null) {
                    setFakedResult(proxiedObj);
                }
            }
        }
    }

    @Override
    protected BaseHookHandle createHookHandle() {
        return new ServiceManagerHookHandle(mHostContext);
    }


}
