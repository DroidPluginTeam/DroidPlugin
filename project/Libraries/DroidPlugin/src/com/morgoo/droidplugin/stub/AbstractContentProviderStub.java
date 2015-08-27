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

package com.morgoo.droidplugin.stub;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;

import com.morgoo.droidplugin.core.Env;
import com.morgoo.droidplugin.core.PluginProcessManager;
import com.morgoo.droidplugin.pm.PluginManager;
import com.morgoo.droidplugin.reflect.FieldUtils;
import com.morgoo.helper.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/2/26.
 */
public abstract class AbstractContentProviderStub extends ContentProvider {

    private static final String TAG = AbstractContentProviderStub.class.getSimpleName();
    private ContentResolver mContentResolver;
    private Map<String, ContentProviderClient> sContentProviderClients = new HashMap<String, ContentProviderClient>();


    @Override
    public boolean onCreate() {
        mContentResolver = getContext().getContentResolver();
        return true;
    }

    private Uri buildNewUri(Uri uri, String targetAuthority) {
        Uri.Builder b = new Builder();
        b.scheme(uri.getScheme());
        b.authority(targetAuthority);
        b.path(uri.getPath());

        if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            Set<String> names = uri.getQueryParameterNames();
            if (names != null && names.size() > 0) {
                for (String name : names) {
                    if (!TextUtils.equals(name, Env.EXTRA_TARGET_AUTHORITY)) {
                        b.appendQueryParameter(name, uri.getQueryParameter(name));
                    }
                }
            }
        } else {
            b.query(uri.getQuery());
        }
        b.fragment(uri.getFragment());
        return b.build();
    }

    private synchronized ContentProviderClient getContentProviderClient(final String targetAuthority) {
        ContentProviderClient client = sContentProviderClients.get(targetAuthority);
        if (client != null) {
            return client;
        }

        if (Looper.getMainLooper() != Looper.myLooper()) {
            PluginManager.getInstance().waitForConnected();
        }

        ProviderInfo stubInfo = null;
        ProviderInfo targetInfo = null;
        try {
            String authority = getMyAuthority();
            stubInfo = getContext().getPackageManager().resolveContentProvider(authority, 0);
            targetInfo = PluginManager.getInstance().resolveContentProvider(targetAuthority, 0);
        } catch (Exception e) {
            Log.e(TAG, "Can not reportMyProcessName on ContentProvider");
        }

        if (stubInfo != null && targetInfo != null) {
            try {
                PluginManager.getInstance().reportMyProcessName(stubInfo.processName, targetInfo.processName, targetInfo.packageName);
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException on reportMyProcessName", e);
            }
        }

        try {
            if (targetInfo != null) {
                PluginProcessManager.preLoadApk(getContext(), targetInfo);
            }
        } catch (Exception e) {
            handleExpcetion(e);
        }


        ContentProviderClient newClient = mContentResolver.acquireContentProviderClient(targetAuthority);
        sContentProviderClients.put(targetAuthority, newClient);

        try {
            if (stubInfo != null && targetInfo != null) {
                PluginManager.getInstance().onProviderCreated(stubInfo, targetInfo);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception on report onProviderCreated", e);
        }

        return sContentProviderClients.get(targetAuthority);
    }

    private String getMyAuthority() throws PackageManager.NameNotFoundException, IllegalAccessException {
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            return (String) FieldUtils.readField(this, "mAuthority");
        } else {
            Context context = getContext();
            PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PROVIDERS);
            if (pkgInfo != null && pkgInfo.providers != null && pkgInfo.providers.length > 0) {
                for (ProviderInfo info : pkgInfo.providers) {
                    if (TextUtils.equals(info.name, getClass().getName())) {
                        return info.authority;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {
        String targetAuthority = uri.getQueryParameter(Env.EXTRA_TARGET_AUTHORITY);
        if (!TextUtils.isEmpty(targetAuthority) && !TextUtils.equals(targetAuthority, uri.getAuthority())) {
            ContentProviderClient client = getContentProviderClient(targetAuthority);
            try {
                return client.query(buildNewUri(uri, targetAuthority), projection, selection, selectionArgs, sortOrder);
            } catch (RemoteException e) {
                handleExpcetion(e);
            }
        }
        return null;
    }

    protected void handleExpcetion(Exception e) {
        Log.e(TAG, "handleExpcetion", e);
    }


    @Override
    public String getType(Uri uri) {
        String targetAuthority = uri.getQueryParameter(Env.EXTRA_TARGET_AUTHORITY);
        if (!TextUtils.isEmpty(targetAuthority) && !TextUtils.equals(targetAuthority, uri.getAuthority())) {
            ContentProviderClient client = getContentProviderClient(targetAuthority);
            try {
                return client.getType(buildNewUri(uri, targetAuthority));
            } catch (RemoteException e) {
                handleExpcetion(e);
            }
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        String targetAuthority = uri.getQueryParameter(Env.EXTRA_TARGET_AUTHORITY);
        if (!TextUtils.isEmpty(targetAuthority) && !TextUtils.equals(targetAuthority, uri.getAuthority())) {
            ContentProviderClient client = getContentProviderClient(targetAuthority);
            try {
                return client.insert(buildNewUri(uri, targetAuthority), contentValues);
            } catch (RemoteException e) {
                handleExpcetion(e);
            }
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String targetAuthority = uri.getQueryParameter(Env.EXTRA_TARGET_AUTHORITY);
        if (!TextUtils.isEmpty(targetAuthority) && !TextUtils.equals(targetAuthority, uri.getAuthority())) {
            ContentProviderClient client = getContentProviderClient(targetAuthority);
            try {
                return client.delete(buildNewUri(uri, targetAuthority), selection, selectionArgs);
            } catch (RemoteException e) {
                handleExpcetion(e);
            }
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        String targetAuthority = uri.getQueryParameter(Env.EXTRA_TARGET_AUTHORITY);
        if (!TextUtils.isEmpty(targetAuthority) && !TextUtils.equals(targetAuthority, uri.getAuthority())) {
            ContentProviderClient client = getContentProviderClient(targetAuthority);
            try {
                return client.update(buildNewUri(uri, targetAuthority), values, selection, selectionArgs);
            } catch (RemoteException e) {
                handleExpcetion(e);
            }
        }
        return 0;
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        String targetAuthority = extras != null ? extras.getString(Env.EXTRA_TARGET_AUTHORITY) : null;
        String targetMethod = extras != null ? extras.getString(Env.EXTRA_TARGET_AUTHORITY) : null;
        if (!TextUtils.isEmpty(targetMethod) && !TextUtils.equals(targetMethod, method)) {
            ContentProviderClient client = getContentProviderClient(targetAuthority);
            try {
                return client.call(targetMethod, arg, extras);
            } catch (RemoteException e) {
                handleExpcetion(e);
            }
        }
        return super.call(method, arg, extras);
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        String targetAuthority = uri.getQueryParameter(Env.EXTRA_TARGET_AUTHORITY);
        if (!TextUtils.isEmpty(targetAuthority) && !TextUtils.equals(targetAuthority, uri.getAuthority())) {
            ContentProviderClient client = getContentProviderClient(targetAuthority);
            try {
                return client.bulkInsert(buildNewUri(uri, targetAuthority), values);
            } catch (RemoteException e) {
                handleExpcetion(e);
            }
        }
        return super.bulkInsert(uri, values);
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
//TODO applyBatch转发
//        if (operations != null && operations.size() > 0) {
//            ArrayList<ContentProviderOperation> OldOperations = new ArrayList<ContentProviderOperation>();
//            Map<String, ArrayList<ContentProviderOperation>> newOperations = new HashMap<String, ArrayList<ContentProviderOperation>>();
//            for (ContentProviderOperation operation : operations) {
//                Uri uri = operation.getUri();
//                String targetAuthority = uri.getQueryParameter(Env.EXTRA_TARGET_AUTHORITY);
//                if (!TextUtils.isEmpty(targetAuthority) && !TextUtils.equals(targetAuthority, uri.getAuthority())) {
//                    try {
//                        Uri newUri = buildNewUri(uri, targetAuthority);
//                        FieldUtils.writeField(operation, "mUri", newUri, true);
//                        ArrayList<ContentProviderOperation> newOps = newOperations.get(targetAuthority);
//                        if (newOps == null) {
//                            newOps = new ArrayList<ContentProviderOperation>(1);
//                            newOps.add(operation);
//                            newOperations.put(targetAuthority, newOps);
//                        } else {
//                            newOps.add(operation);
//                        }
//                    } catch (IllegalAccessException e) {
//                        handleExpcetion(e);
//                    }
//                } else {
//                    OldOperations.add(operation);
//                }
//            }
//
//            if (newOperations.size() > 0) {
//                ArrayList<ContentProviderResult> results = new ArrayList<ContentProviderResult>(operations.size());
//                for (String authority : newOperations.keySet()) {
//                    ContentProviderClient client = getContentProviderClient(authority);
//                    ArrayList<ContentProviderOperation> contentProviderOperations = newOperations.get(authority);
//                    if (contentProviderOperations.size() > 0) {
//                        ContentProviderResult[] rs = client.applyBatch(contentProviderOperations);
//                        for (ContentProviderResult r : rs) {
//                            results.add(r);
//                        }
//                    }
//                }
//                //这一步必须要在主线程中执行，这里还有bug
//
//            }
//        }
        return super.applyBatch(operations);
    }


}
