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

package com.morgoo.droidplugin.hook.proxy;

import android.content.Context;
import android.content.pm.ProviderInfo;

import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.handle.IContentProviderInvokeHandle;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/3/6.
 */
public class IContentProviderHook extends ProxyHook {


    private final ProviderInfo mStubProvider;
    private final ProviderInfo mTargetProvider;
    private final boolean mLocalProvider;


    public IContentProviderHook(Context context, Object oldObj, ProviderInfo stubProvider, ProviderInfo targetProvider, boolean localProvider) {
        super(context);
        setOldObj(oldObj);
        mStubProvider = stubProvider;
        mTargetProvider = targetProvider;
        mLocalProvider = localProvider;
        mHookHandles = createHookHandle();

    }

    @Override
    protected BaseHookHandle createHookHandle() {
        return new IContentProviderInvokeHandle(mHostContext, mStubProvider, mTargetProvider, mLocalProvider);
    }

    @Override
    protected void onInstall(ClassLoader classLoader) throws Throwable {
    }
}
