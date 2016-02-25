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

import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.handle.ILocationManagerHookHandle;
import com.morgoo.helper.compat.ILocationManagerCompat;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2016/2/25.
 */
public class ILocationManagerBinderHook extends BinderHook {


    private final static String SERVICE_NAME = Context.LOCATION_SERVICE;

    public ILocationManagerBinderHook(Context hostContext) {
        super(hostContext);
    }

    @Override
    Object getOldObj() throws Exception {
        IBinder iBinder = MyServiceManager.getOriginService(SERVICE_NAME);
        return ILocationManagerCompat.asInterface(iBinder);
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    protected BaseHookHandle createHookHandle() {
        return new ILocationManagerHookHandle(mHostContext);
    }
}
