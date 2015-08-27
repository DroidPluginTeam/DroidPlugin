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
import android.view.inputmethod.EditorInfo;

import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.HookedMethodHandler;

import java.lang.reflect.Method;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/6/4.
 */
public class IInputMethodManagerHookHandle extends BaseHookHandle {

    public IInputMethodManagerHookHandle(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected void init() {
        sHookedMethodHandlers.put("startInput", new startInput(mHostContext));
        sHookedMethodHandlers.put("windowGainedFocus", new windowGainedFocus(mHostContext));
    }

    private class IInputMethodManagerHookedMethodHandler extends HookedMethodHandler {
        public IInputMethodManagerHookedMethodHandler(Context hostContext) {
            super(hostContext);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            if (args != null && args.length > 0) {
                for (Object arg : args) {
                    if (arg instanceof EditorInfo) {
                        EditorInfo info = ((EditorInfo) arg);
                        if (!TextUtils.equals(mHostContext.getPackageName(), info.packageName)) {
                            info.packageName = mHostContext.getPackageName();
                        }
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class startInput extends IInputMethodManagerHookedMethodHandler {
        public startInput(Context hostContext) {
            super(hostContext);
        }
    }

    private class windowGainedFocus extends IInputMethodManagerHookedMethodHandler {
        public windowGainedFocus(Context hostContext) {
            super(hostContext);
        }
    }
}
