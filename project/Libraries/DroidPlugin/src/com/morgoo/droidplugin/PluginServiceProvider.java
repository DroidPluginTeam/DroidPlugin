package com.morgoo.droidplugin;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.morgoo.helper.compat.BundleCompat;


public class PluginServiceProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return uri.getPath();
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        if (Method_GetManager.equals(method)) {
            Uri uri = null;
            if (extras != null && extras.containsKey(URI_VALUE)) {
                uri = Uri.parse(extras.getString(URI_VALUE));
            }
            Bundle bundle = new Bundle();
            BundleCompat.putBinder(bundle, Arg_Binder, PluginManagerService.getPluginPackageManager(getContext()));
            return bundle;
        }
        return null;
    }
    public static final String Method_GetManager = "getPluginManager";
    public static final String Arg_Binder = "_binder_";
    public static final String URI_KEY = "key";
    public static final String URI_VALUE = "uri";
}
