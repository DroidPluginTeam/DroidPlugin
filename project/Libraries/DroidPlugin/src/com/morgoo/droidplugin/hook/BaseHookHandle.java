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

package com.morgoo.droidplugin.hook;

import android.content.Context;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/2/28.
 */
public abstract class BaseHookHandle {

    protected Context mHostContext;

    protected Map<String, HookedMethodHandler> sHookedMethodHandlers = new HashMap<String, HookedMethodHandler>(5);

    public BaseHookHandle(Context hostContext) {
        mHostContext = hostContext;
        init();
    }

    protected abstract void init();

    public Set<String> getHookedMethodNames(){
        return sHookedMethodHandlers.keySet();
    }

    public HookedMethodHandler getHookedMethodHandler(Method method) {
        if (method != null) {
            return sHookedMethodHandlers.get(method.getName());
        } else {
            return null;
        }
    }
}
