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

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/3/2.
 */
public abstract class Hook {

    private boolean mEnable = false;

    protected Context mHostContext;
    protected BaseHookHandle mHookHandles;

    public void setEnable(boolean enable, boolean reInstallHook) {
        this.mEnable = enable;
    }

    public final void setEnable(boolean enable) {
        setEnable(enable, false);
    }

    public boolean isEnable() {
        return mEnable;
    }


    protected Hook(Context hostContext) {
        mHostContext = hostContext;
        mHookHandles = createHookHandle();
    }

    protected abstract BaseHookHandle createHookHandle();


    protected abstract void onInstall(ClassLoader classLoader) throws Throwable;

    protected void onUnInstall(ClassLoader classLoader) throws Throwable {

    }
}
