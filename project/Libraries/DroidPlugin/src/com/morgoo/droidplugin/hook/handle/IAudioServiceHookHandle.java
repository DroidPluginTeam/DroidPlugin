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
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.text.TextUtils;

import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.HookedMethodHandler;
import com.morgoo.droidplugin.pm.PluginManager;

import java.lang.reflect.Method;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/3/6.
 */
public class IAudioServiceHookHandle extends BaseHookHandle {

    public IAudioServiceHookHandle(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected void init() {
        sHookedMethodHandlers.put("adjustVolume", new adjustVolume(mHostContext));
        sHookedMethodHandlers.put("adjustLocalOrRemoteStreamVolume", new adjustLocalOrRemoteStreamVolume(mHostContext));
        sHookedMethodHandlers.put("adjustSuggestedStreamVolume", new adjustSuggestedStreamVolume(mHostContext));
        sHookedMethodHandlers.put("adjustStreamVolume", new adjustStreamVolume(mHostContext));
        sHookedMethodHandlers.put("adjustMasterVolume", new adjustMasterVolume(mHostContext));
        sHookedMethodHandlers.put("setStreamVolume", new setStreamVolume(mHostContext));
        sHookedMethodHandlers.put("setMasterVolume", new setMasterVolume(mHostContext));
        sHookedMethodHandlers.put("requestAudioFocus", new requestAudioFocus(mHostContext));
        sHookedMethodHandlers.put("registerRemoteControlClient", new registerRemoteControlClient(mHostContext));

    }

    private static class MyBaseHandler extends HookedMethodHandler {

        public MyBaseHandler(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                if (args != null && args.length > 0) {
                    for (int index = 0; index < args.length; index++) {
                        if (args[index] instanceof String) {
                            String callingPkg = (String) args[index];
                            if (!TextUtils.equals(callingPkg, mHostContext.getPackageName()) && PluginManager.getInstance().isPluginPackage(callingPkg)) {
                                args[index] = mHostContext.getPackageName();
                            }
                        }
                    }
                }

            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private static class adjustVolume extends MyBaseHandler {
        public adjustVolume(Context context) {
            super(context);
        }
    }

    private static class adjustLocalOrRemoteStreamVolume extends MyBaseHandler {
        public adjustLocalOrRemoteStreamVolume(Context context) {
            super(context);
        }
    }

    private static class adjustSuggestedStreamVolume extends MyBaseHandler {
        public adjustSuggestedStreamVolume(Context context) {
            super(context);
        }
    }

    private static class adjustStreamVolume extends MyBaseHandler {
        public adjustStreamVolume(Context context) {
            super(context);
        }
    }

    private static class adjustMasterVolume extends MyBaseHandler {
        public adjustMasterVolume(Context context) {
            super(context);
        }
    }

    private static class setStreamVolume extends MyBaseHandler {
        public setStreamVolume(Context context) {
            super(context);
        }
    }

    private static class setMasterVolume extends MyBaseHandler {
        public setMasterVolume(Context context) {
            super(context);
        }
    }

    private static class requestAudioFocus extends MyBaseHandler {
        public requestAudioFocus(Context context) {
            super(context);
        }
    }

    private static class registerRemoteControlClient extends MyBaseHandler {
        public registerRemoteControlClient(Context context) {
            super(context);
        }
    }
}
