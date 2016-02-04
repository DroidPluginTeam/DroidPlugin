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

import android.os.Build;

import com.morgoo.droidplugin.reflect.MethodUtils;
import com.morgoo.helper.Log;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2016/2/4.
 */
public class VMRuntimeCompat {

    private static final String TAG = VMRuntimeCompat.class.getSimpleName();

    public final static boolean is64Bit() {
        //  dalvik.system.VMRuntime.getRuntime().is64Bit();
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                return false;
            }
            Class VMRuntime = Class.forName("dalvik.system.VMRuntime");
            Object VMRuntimeObj = MethodUtils.invokeStaticMethod(VMRuntime, "getRuntime");
            Object is64Bit = MethodUtils.invokeMethod(VMRuntimeObj, "is64Bit");
            if (is64Bit instanceof Boolean) {
                return ((Boolean) is64Bit);
            }
        } catch (Throwable e) {
            Log.w(TAG, "is64Bit", e);
        }
        return false;
    }
}
