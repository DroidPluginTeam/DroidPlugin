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
import android.content.pm.ProviderInfo;
import android.net.Uri;

import com.morgoo.droidplugin.core.Env;
import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.HookedMethodHandler;
import com.morgoo.droidplugin.pm.PluginManager;
import com.morgoo.helper.Log;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/5/21.
 */
public class IContentServiceHandle extends BaseHookHandle {

    private static final String TAG = IContentServiceHandle.class.getSimpleName();

    public IContentServiceHandle(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected void init() {
        sHookedMethodHandlers.put("registerContentObserver", new registerContentObserver(mHostContext));
        sHookedMethodHandlers.put("notifyChange", new notifyChange(mHostContext));
    }

    private static class IContentServiceHookedMethodHandler extends HookedMethodHandler {


        public IContentServiceHookedMethodHandler(Context hostContext) {
            super(hostContext);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            if (args != null) {
                final int index = 1;
                if (args.length > index && args[index] instanceof Uri) {
                    Uri uri = (Uri) args[index];
                    String authority = uri.getAuthority();
                    ProviderInfo provider = PluginManager.getInstance().resolveContentProvider(authority, 0);
                    if (provider != null) {
                        ProviderInfo info = PluginManager.getInstance().selectStubProviderInfo(authority);
                        Uri.Builder newUri = new Uri.Builder();
                        newUri.scheme("content");
                        newUri.authority(uri.getAuthority());
                        newUri.path(uri.getPath());
                        newUri.query(uri.getQuery());
                        newUri.appendQueryParameter(Env.EXTRA_TARGET_AUTHORITY,authority);
                        args[index] = newUri.build();
//                        return true;
                    } else {
                        Log.w(TAG, "getContentProvider,fake fail 2=%s", authority);
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private static class registerContentObserver extends IContentServiceHookedMethodHandler {
        public registerContentObserver(Context hostContext) {
            super(hostContext);
        }
    }

    private static class notifyChange extends IContentServiceHookedMethodHandler {
        public notifyChange(Context hostContext) {
            super(hostContext);
        }
    }
}
