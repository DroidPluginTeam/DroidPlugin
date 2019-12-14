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
import android.view.WindowManager;

import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.HookedMethodHandler;
import com.morgoo.droidplugin.pm.PluginManager;

import java.lang.reflect.Method;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/6/17.
 */
public class IWindowSessionInvokeHandle extends BaseHookHandle {

    public IWindowSessionInvokeHandle(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected void init() {
        sHookedMethodHandlers.put("add", new add(mHostContext));
        sHookedMethodHandlers.put("addToDisplay", new addToDisplay(mHostContext));
        sHookedMethodHandlers.put("addWithoutInputChannel", new addWithoutInputChannel(mHostContext));
        sHookedMethodHandlers.put("addToDisplayWithoutInputChannel", new addToDisplayWithoutInputChannel(mHostContext));
        sHookedMethodHandlers.put("relayout", new relayout(mHostContext));
    }

    private class IWindowSessionHookedMethodHandler extends HookedMethodHandler {
        public IWindowSessionHookedMethodHandler(Context hostContext) {
            super(hostContext);
        }

        int findWindowManagerLayoutParamsIndex(Object[] args) {
            if (args != null && args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof WindowManager.LayoutParams) {
                        return i;
                    }
                }
            }
            return -1;

        }


        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            if (args != null && args.length > 0) {
                int index = findWindowManagerLayoutParamsIndex(args);
                if (index >= 0) {
                    WindowManager.LayoutParams attr = ((WindowManager.LayoutParams) args[index]);
//                    String oldPkg = attr.packageName;
                    if (!TextUtils.equals(attr.packageName, mHostContext.getPackageName())) {
                        attr.packageName = mHostContext.getPackageName();
                    }
//                    CharSequence ctitle = attr.getTitle();
//                    String title = String.valueOf(ctitle);
//                    if (title != null && title.startsWith(oldPkg + "/")) {
//                        String newTitle = title.replace(oldPkg + "/", attr.packageName + "/");
//                        attr.setTitle(newTitle);
//                    }

                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class add extends IWindowSessionHookedMethodHandler {
        public add(Context hostContext) {
            super(hostContext);
        }
    }

    private class addToDisplay extends IWindowSessionHookedMethodHandler {
        public addToDisplay(Context hostContext) {
            super(hostContext);
        }
    }

    private class addWithoutInputChannel extends IWindowSessionHookedMethodHandler {
        public addWithoutInputChannel(Context hostContext) {
            super(hostContext);
        }
    }

    private class addToDisplayWithoutInputChannel extends IWindowSessionHookedMethodHandler {
        public addToDisplayWithoutInputChannel(Context hostContext) {
            super(hostContext);
        }
    }

    private class relayout extends IWindowSessionHookedMethodHandler {
        public relayout(Context hostContext) {
            super(hostContext);
        }
    }
}
