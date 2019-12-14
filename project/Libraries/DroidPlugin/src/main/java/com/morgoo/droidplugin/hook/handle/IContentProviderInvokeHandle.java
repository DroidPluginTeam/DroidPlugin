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
import android.net.Uri.Builder;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.text.TextUtils;

import com.morgoo.droidplugin.core.Env;
import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.HookedMethodHandler;

import java.lang.reflect.Method;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/3/6.
 */
public class IContentProviderInvokeHandle extends BaseHookHandle {
    private final ProviderInfo mStubProvider;
    private final ProviderInfo mTargetProvider;
    private final boolean mLocalProvider;

    public IContentProviderInvokeHandle(Context hostContext, ProviderInfo stubProvider, ProviderInfo targetProvider, boolean localProvider) {
        super(hostContext);
        mStubProvider = stubProvider;
        mTargetProvider = targetProvider;
        mLocalProvider = localProvider;
    }


    @Override
    protected void init() {
        sHookedMethodHandlers.put("query", new query(mHostContext));
        sHookedMethodHandlers.put("getType", new getType(mHostContext));
        sHookedMethodHandlers.put("insert", new insert(mHostContext));
        sHookedMethodHandlers.put("bulkInsert", new bulkInsert(mHostContext));
        sHookedMethodHandlers.put("delete", new delete(mHostContext));
        sHookedMethodHandlers.put("update", new update(mHostContext));
        sHookedMethodHandlers.put("openFile", new openFile(mHostContext));
        sHookedMethodHandlers.put("openAssetFile", new openAssetFile(mHostContext));
        sHookedMethodHandlers.put("applyBatch", new applyBatch(mHostContext));
        sHookedMethodHandlers.put("call", new call(mHostContext));
        sHookedMethodHandlers.put("createCancellationSignal", new createCancellationSignal(mHostContext));
        sHookedMethodHandlers.put("canonicalize", new canonicalize(mHostContext));
        sHookedMethodHandlers.put("uncanonicalize", new uncanonicalize(mHostContext));
        sHookedMethodHandlers.put("getStreamTypes", new getStreamTypes(mHostContext));
        sHookedMethodHandlers.put("openTypedAssetFile", new openTypedAssetFile(mHostContext));
    }

    private class MyHandler extends ReplaceCallingPackageHookedMethodHandler {

        public MyHandler(Context hostContext) {
            super(hostContext);
        }

        private int indexFirstUri(Object[] args) {
            if (args != null && args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Uri) {
                        return i;
                    }
                }
            }
            return -1;
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
//            if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2) {
//                final int index = 0;
//                if (args != null && args.length > index && args[index] instanceof String) {
//                    String pkg = (String) args[index];
//                    if (!TextUtils.equals(pkg, mHostContext.getPackageName())) {
//                        args[index] = mHostContext.getPackageName();
//                    }
//                }
//            }
            if (!mLocalProvider && mStubProvider != null) {
                final int index = indexFirstUri(args);
                if (index >= 0) {
                    Uri uri = (Uri) args[index];
                    String authority = uri.getAuthority();
                    if (!TextUtils.equals(authority, mStubProvider.authority)) {
                        Uri.Builder b = new Builder();
                        b.scheme(uri.getScheme());
                        b.authority(mStubProvider.authority);
                        b.path(uri.getPath());
                        b.query(uri.getQuery());
                        b.appendQueryParameter(Env.EXTRA_TARGET_AUTHORITY, authority);
                        b.fragment(uri.getFragment());
                        args[index] = b.build();
                    }
                }
            }

            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class query extends MyHandler {
        public query(Context context) {
            super(context);
        }
    }

    private class getType extends MyHandler {
        public getType(Context context) {
            super(context);
        }
    }

    private class insert extends MyHandler {
        public insert(Context context) {
            super(context);
        }
    }

    private class bulkInsert extends MyHandler {
        public bulkInsert(Context context) {
            super(context);
        }
    }

    private class delete extends MyHandler {
        public delete(Context context) {
            super(context);
        }
    }

    private class update extends MyHandler {
        public update(Context context) {
            super(context);
        }
    }

    private class openFile extends MyHandler {
        public openFile(Context context) {
            super(context);
        }
    }

    private class openAssetFile extends MyHandler {
        public openAssetFile(Context context) {
            super(context);
        }
    }

    private class applyBatch extends MyHandler {
        public applyBatch(Context context) {
            super(context);
        }
    }

    private class call extends MyHandler {
        public call(Context context) {
            super(context);
        }
    }

    private class createCancellationSignal extends MyHandler {
        public createCancellationSignal(Context context) {
            super(context);
        }
    }

    private class canonicalize extends MyHandler {
        public canonicalize(Context context) {
            super(context);
        }
    }

    private class uncanonicalize extends MyHandler {
        public uncanonicalize(Context context) {
            super(context);
        }
    }

    private class getStreamTypes extends MyHandler {
        public getStreamTypes(Context context) {
            super(context);
        }
    }

    private class openTypedAssetFile extends MyHandler {
        public openTypedAssetFile(Context context) {
            super(context);
        }
    }
}
