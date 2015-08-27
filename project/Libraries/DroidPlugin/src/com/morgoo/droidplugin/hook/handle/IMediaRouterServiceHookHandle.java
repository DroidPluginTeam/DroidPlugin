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
import android.text.TextUtils;
import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.HookedMethodHandler;

import java.lang.reflect.Method;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/3/6.
 */
public class IMediaRouterServiceHookHandle extends BaseHookHandle {

    public IMediaRouterServiceHookHandle(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected void init() {
//        public void registerClientAsUser(IMediaRouterClient client, String packageName, int userId);
//        public void unregisterClient(IMediaRouterClient client);
//        public MediaRouterClientState getState(IMediaRouterClient client);
//        public void setDiscoveryRequest(IMediaRouterClient client, int routeTypes, boolean activeScan);
//        public void setSelectedRoute(IMediaRouterClient client, String routeId, boolean explicit);
//        public void requestSetVolume(IMediaRouterClient client, String routeId, int volume);
//        public void requestUpdateVolume(IMediaRouterClient client, String routeId, int direction);
        sHookedMethodHandlers.put("registerClientAsUser", new registerClientAsUser(mHostContext));
    }


    private class registerClientAsUser extends HookedMethodHandler {
        public registerClientAsUser(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            int index = 1;
            if (args != null && args.length > index && args[index] instanceof String) {
                String pkg = (String) args[index];
                if (!TextUtils.equals(pkg, mHostContext.getPackageName())) {
                    args[index] = mHostContext.getPackageName();
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }
}
