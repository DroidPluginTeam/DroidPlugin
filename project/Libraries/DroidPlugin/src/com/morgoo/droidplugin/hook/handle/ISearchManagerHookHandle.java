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

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;

import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.HookedMethodHandler;
import com.morgoo.droidplugin.pm.PluginManager;
import com.morgoo.helper.Log;

import java.lang.reflect.Method;

/**
 * Created by wyw on 15-9-18.
 */
public class ISearchManagerHookHandle extends BaseHookHandle {

    public ISearchManagerHookHandle(Context context) {
        super(context);
    }
    @Override
    protected void init() {
        sHookedMethodHandlers.put("getSearchableInfo", new getSearchableInfo(mHostContext));

    }

    private class getSearchableInfo extends HookedMethodHandler{

        public getSearchableInfo(Context hostContext) {
            super(hostContext);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            if (args != null && args.length > 0 && args[args.length - 1] instanceof ComponentName) {
                ComponentName cpn = (ComponentName) args[args.length - 1];
                ActivityInfo info = PluginManager.getInstance().getActivityInfo(cpn, 0);
                if (info != null) {
                    ActivityInfo proxyInfo = PluginManager.getInstance().selectStubActivityInfo(info);
                    if (proxyInfo != null) {
                        args[args.length - 1] = new ComponentName(proxyInfo.packageName, proxyInfo.name);
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }
}
