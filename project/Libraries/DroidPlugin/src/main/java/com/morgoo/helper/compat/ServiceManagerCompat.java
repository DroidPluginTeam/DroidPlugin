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

package com.morgoo.helper.compat;

import android.os.IBinder;

import com.morgoo.droidplugin.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/4/13.
 */
public class ServiceManagerCompat {

    private static Class sClass = null;

    public static final Class Class() throws ClassNotFoundException {
        if (sClass == null) {
            sClass = Class.forName("android.os.ServiceManager");
        }
        return sClass;
    }


    public static IBinder getService(String name) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
       return (IBinder) MethodUtils.invokeStaticMethod(Class(), "getService", name);
    }
}
