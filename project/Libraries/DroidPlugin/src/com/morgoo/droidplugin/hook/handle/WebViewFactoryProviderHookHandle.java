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
import android.os.Build;
import android.webkit.WebView;

import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.HookedMethodHandler;
import com.morgoo.droidplugin.reflect.MethodUtils;
import com.morgoo.droidplugin.reflect.Utils;
import com.morgoo.helper.MyProxy;
import com.morgoo.helper.compat.WebViewFactoryCompat;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2014/10/10.
 */
public class WebViewFactoryProviderHookHandle extends BaseHookHandle {

    public WebViewFactoryProviderHookHandle(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected void init() {
        sHookedMethodHandlers.put("createWebView", new createWebView(mHostContext));
    }

    private static Class sContentMain;

    private static void fixWebViewAsset(Context context) {
        try {
            if (sContentMain == null) {
                Object provider = WebViewFactoryCompat.getProvider();
                if (provider != null) {
                    ClassLoader cl = provider.getClass().getClassLoader();
                    sContentMain = Class.forName("org.chromium.content.app.ContentMain", true, cl);
                }
            }
            if (sContentMain != null) {
                MethodUtils.invokeStaticMethod(sContentMain, "initApplicationContext", context.getApplicationContext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class createWebView extends HookedMethodHandler {

        protected WebView mWebView;

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            final int index = 0;
            if (args != null && args.length > index && args[index] instanceof WebView) {
                mWebView = (WebView) args[0];
            }
            return super.beforeInvoke(receiver, method, args);
        }

        public createWebView(Context context) {
            super(context);
        }


        @Override
        protected void afterInvoke(Object receiver, Method method, Object[] args, final Object invokeResult) throws Throwable {
            if (mWebView != null) {
                fixWebViewAsset(mWebView.getContext());
                Class clazz = invokeResult.getClass();
                List<Class<?>> interfaces = Utils.getAllInterfaces(clazz);
                Class[] ifs = interfaces != null && interfaces.size() > 0 ? interfaces.toArray(new Class[interfaces.size()]) : new Class[0];
                final Object newObj = MyProxy.newProxyInstance(clazz.getClassLoader(), ifs, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Object invoke = method.invoke(invokeResult, args);
                        fixWebViewAsset(mWebView.getContext());
                        return invoke;
                    }
                });
                setFakedResult(newObj);
            }
            super.afterInvoke(receiver, method, args, invokeResult);
        }
    }
}