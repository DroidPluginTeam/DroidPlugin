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

import android.os.IBinder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/3/2.
 */
public class MyServiceManager {

    private static Map<String, IBinder> mOriginServiceCache = new HashMap<String, IBinder>(1);
    private static Map<String, IBinder> mProxiedServiceCache = new HashMap<String, IBinder>(1);
    private static Map<String, Object> mProxiedObjCache = new HashMap<String, Object>(1);

    static IBinder getOriginService(String serviceName) {
        return mOriginServiceCache.get(serviceName);
    }

    public static void addOriginService(String serviceName, IBinder service) {
        mOriginServiceCache.put(serviceName, service);
    }

    static  void addProxiedServiceCache(String serviceName, IBinder proxyService) {
        mProxiedServiceCache.put(serviceName, proxyService);
    }

    static Object getProxiedObj(String servicename) {
        return mProxiedObjCache.get(servicename);
    }

    static void addProxiedObj(String servicename, Object obj) {
        mProxiedObjCache.put(servicename, obj);
    }
}
