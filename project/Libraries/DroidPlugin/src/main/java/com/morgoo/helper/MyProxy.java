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

package com.morgoo.helper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.SocketException;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com)ClassUtils on 2015/3/25.
 */
public class MyProxy {

    public static Object newProxyInstance(ClassLoader loader, Class<?>[] interfaces,
                                          InvocationHandler invocationHandler) {
        return Proxy.newProxyInstance(loader, interfaces,
                invocationHandler);
    }

    /**
     * 判断某个异常是否已经在某个方法上声明了。
     */
    public static boolean isMethodDeclaredThrowable(Method method, Throwable e) {
        if (e instanceof RuntimeException) {
            return true;
        }

        if (method == null || e == null) {
            return false;
        }

        Class[] es = method.getExceptionTypes();
        if (es == null && es.length <= 0) {
            return false;
        }

//bugfix,这个问题我也不知道为什么出现，先这么处理吧。
//        java.lang.RuntimeException: Socket closed
//        at com.morgoo.droidplugin.c.c.i.invoke(Unknown Source)
//        at $Proxy9.accept(Native Method)
//        at java.net.PlainSocketImpl.accept(PlainSocketImpl.java:98)
//        at java.net.ServerSocket.implAccept(ServerSocket.java:202)
//        at java.net.ServerSocket.accept(ServerSocket.java:127)
//        at com.qihoo.appstore.h.b.run(Unknown Source)
//        at java.lang.Thread.run(Thread.java:864)
//        Caused by: java.net.SocketException: Socket closed
//        at libcore.io.Posix.accept(Native Method)
//        at libcore.io.BlockGuardOs.accept(BlockGuardOs.java:55)
//        at java.lang.reflect.Method.invokeNative(Native Method)
//        at java.lang.reflect.Method.invoke(Method.java:511)
//        ... 7 more
        try {
            String methodName = method.getName();
            boolean va = "accept".equals(methodName) || "sendto".equals(methodName);
            if (e instanceof SocketException && va && method.getDeclaringClass().getName().indexOf("libcore") >= 0) {
                return true;
            }
        } catch (Throwable e1) {
            //DO NOTHING
        }

        for (Class aClass : es) {
            if (aClass.isInstance(e) || aClass.isAssignableFrom(e.getClass())) {
                return true;
            }
        }
        return false;
    }
}
