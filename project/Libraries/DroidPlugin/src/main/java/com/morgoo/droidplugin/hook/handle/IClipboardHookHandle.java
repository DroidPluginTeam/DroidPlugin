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
public class IClipboardHookHandle extends BaseHookHandle {

    public IClipboardHookHandle(Context context) {
        super(context);
    }

    //17
//    void setPrimaryClip(in ClipData clip);
//    ClipData getPrimaryClip(String pkg);
//    ClipDescription getPrimaryClipDescription();
//    boolean hasPrimaryClip();
//    void addPrimaryClipChangedListener(in IOnPrimaryClipChangedListener listener);
//    void removePrimaryClipChangedListener(in IOnPrimaryClipChangedListener listener);
//    boolean hasClipboardText();

    //API 21,19,18
//    void setPrimaryClip(ClipData clip, String callingPackage);
//    ClipData getPrimaryClip(String pkg);
//    ClipDescription getPrimaryClipDescription(String callingPackage);
//    boolean hasPrimaryClip(String callingPackage);
//    void addPrimaryClipChangedListener(IOnPrimaryClipChangedListener listener, String callingPackage);
//    void removePrimaryClipChangedListener(IOnPrimaryClipChangedListener listener);
//    boolean hasClipboardText(String callingPackage);


    @Override
    protected void init() {
        sHookedMethodHandlers.put("setPrimaryClip", new setPrimaryClip(mHostContext));
        sHookedMethodHandlers.put("getPrimaryClip", new getPrimaryClip(mHostContext));
        sHookedMethodHandlers.put("getPrimaryClipDescription", new getPrimaryClipDescription(mHostContext));
        sHookedMethodHandlers.put("hasPrimaryClip", new hasPrimaryClip(mHostContext));
        sHookedMethodHandlers.put("addPrimaryClipChangedListener", new addPrimaryClipChangedListener(mHostContext));
        sHookedMethodHandlers.put("removePrimaryClipChangedListener", new removePrimaryClipChangedListener(mHostContext));
        sHookedMethodHandlers.put("hasClipboardText", new hasClipboardText(mHostContext));
    }

    private class MyBaseHookedMethodHandler extends HookedMethodHandler {
        public MyBaseHookedMethodHandler(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            if (args != null && args.length > 0 && args[args.length - 1] instanceof String) {
                String pkg = (String) args[args.length - 1];
                if (!TextUtils.equals(pkg, mHostContext.getPackageName())) {
                    args[args.length - 1] = mHostContext.getPackageName();
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class setPrimaryClip extends MyBaseHookedMethodHandler {
        public setPrimaryClip(Context context) {
            super(context);
        }
    }

    private class getPrimaryClip extends MyBaseHookedMethodHandler {
        public getPrimaryClip(Context context) {
            super(context);
        }
    }

    private class getPrimaryClipDescription extends MyBaseHookedMethodHandler {
        public getPrimaryClipDescription(Context context) {
            super(context);
        }
    }

    private class hasPrimaryClip extends MyBaseHookedMethodHandler {
        public hasPrimaryClip(Context context) {
            super(context);
        }
    }

    private class addPrimaryClipChangedListener extends MyBaseHookedMethodHandler {
        public addPrimaryClipChangedListener(Context context) {
            super(context);
        }
    }

    private class removePrimaryClipChangedListener extends MyBaseHookedMethodHandler {
        public removePrimaryClipChangedListener(Context context) {
            super(context);
        }
    }

    private class hasClipboardText extends MyBaseHookedMethodHandler {
        public hasClipboardText(Context context) {
            super(context);
        }
    }
}
