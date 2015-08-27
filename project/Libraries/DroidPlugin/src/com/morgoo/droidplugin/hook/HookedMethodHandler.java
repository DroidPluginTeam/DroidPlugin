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
import com.morgoo.helper.Log;

import java.lang.reflect.Method;

public class HookedMethodHandler {

    private static final String TAG = HookedMethodHandler.class.getSimpleName();
    protected final Context mHostContext;

    private Object mFakedResult = null;
    private boolean mUseFakedResult = false;

    public HookedMethodHandler(Context hostContext) {
        this.mHostContext = hostContext;
    }


    public synchronized Object doHookInner(Object receiver, Method method, Object[] args) throws Throwable {
        long b = System.currentTimeMillis();
        try {
            mUseFakedResult = false;
            mFakedResult = null;
            boolean suc = beforeInvoke(receiver, method, args);
            Object invokeResult = null;
            if (!suc) {
                invokeResult = method.invoke(receiver, args);
            }
            afterInvoke(receiver, method, args, invokeResult);
            if (mUseFakedResult) {
                return mFakedResult;
            } else {
                return invokeResult;
            }
        } finally {
            long time = System.currentTimeMillis() - b;
            if (time > 5) {
                Log.i(TAG, "doHookInner method(%s.%s) cost %s ms", method.getDeclaringClass().getName(), method.getName(), time);
            }
        }
    }

    public void setFakedResult(Object fakedResult) {
        this.mFakedResult = fakedResult;
        mUseFakedResult = true;
    }

    /**
     * 在某个方法被调用之前执行，如果返回true，则不执行原始的方法，否则执行原始方法
     */
    protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
        return false;
    }

    protected void afterInvoke(Object receiver, Method method, Object[] args, Object invokeResult) throws Throwable {
    }

    public boolean isFakedResult() {
        return mUseFakedResult;
    }

    public Object getFakedResult() {
        return mFakedResult;
    }
}
