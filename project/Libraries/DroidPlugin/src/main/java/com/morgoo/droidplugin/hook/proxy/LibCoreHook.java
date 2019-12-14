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

package com.morgoo.droidplugin.hook.proxy;

import android.content.Context;

import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.handle.LibCoreHookHandle;
import com.morgoo.droidplugin.reflect.FieldUtils;
import com.morgoo.helper.Log;
import com.morgoo.helper.MyProxy;

import java.util.ArrayList;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/3/30.
 */
public class LibCoreHook extends ProxyHook {

    private static final String TAG = LibCoreHook.class.getSimpleName();

    public LibCoreHook(Context hostContext) throws ClassNotFoundException {
        super(hostContext);
    }

    @Override
    protected BaseHookHandle createHookHandle() {
        return new LibCoreHookHandle(mHostContext);
    }

    private Class<?>[] getAllInterfaces(Class clz) {
        ArrayList<Class> re = new ArrayList<Class>();
        Class<?>[] ifss = clz.getInterfaces();
        for (Class<?> ifs : ifss) {
            if (!re.contains(ifs)) {
                re.add(ifs);
            }
        }

        Class superclass = clz.getSuperclass();
        while (superclass != null) {
            Class<?>[] sifss = superclass.getInterfaces();
            for (Class<?> ifs : sifss) {
                if (!re.contains(ifs)) {
                    re.add(ifs);
                }
            }
            superclass = superclass.getSuperclass();
        }
        return re.toArray(new Class[re.size()]);
    }

    @Override
    protected void onInstall(ClassLoader classLoader) throws Throwable {
        if (!installHook1()) {
            installHook2();
        }
    }

    private boolean installHook1() {
        try {
            Class LibCore = Class.forName("libcore.io.Libcore");
            Object LibCoreOs = FieldUtils.readStaticField(LibCore, "os");
            Object Posix = FieldUtils.readField(LibCoreOs, "os", true);
            setOldObj(Posix);
            Class<?> aClass = mOldObj.getClass();
            Class<?>[] interfaces = getAllInterfaces(aClass);
            Object proxyObj = MyProxy.newProxyInstance(mOldObj.getClass().getClassLoader(), interfaces, this);
            FieldUtils.writeField(LibCoreOs, "os", proxyObj, true);
            return true;
        } catch (Throwable e) {
            Log.w(TAG, "installHook2 fail", e);
        }
        return false;
    }

    private void installHook2() throws ClassNotFoundException, IllegalAccessException {
        Class LibCore = Class.forName("libcore.io.Libcore");
        Object oldObj = FieldUtils.readStaticField(LibCore, "os");
        setOldObj(oldObj);
        Class<?> aClass = mOldObj.getClass();
        Class<?>[] interfaces = getAllInterfaces(aClass);
        Object proxyObj = MyProxy.newProxyInstance(mOldObj.getClass().getClassLoader(), interfaces, this);
        FieldUtils.writeStaticField(LibCore, "os", proxyObj);
    }
}
