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
import com.morgoo.droidplugin.hook.handle.IContentServiceHandle;
import com.morgoo.helper.compat.IContentServiceCompat;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/5/21.
 */
public class IContentServiceBinderHook extends BinderHook {

    private static final String CONTENT_SERVICE_NAME = "content";

    public IContentServiceBinderHook(Context hostContext) {
        super(hostContext);
    }

    @Override
    public Object getOldObj() throws Exception {
        IBinder iBinder = MyServiceManager.getOriginService(CONTENT_SERVICE_NAME);
        return IContentServiceCompat.asInterface(iBinder);
    }

    @Override
    public String getServiceName() {
        return CONTENT_SERVICE_NAME;
    }

    @Override
    protected BaseHookHandle createHookHandle() {
        return new IContentServiceHandle(mHostContext);
    }
}
