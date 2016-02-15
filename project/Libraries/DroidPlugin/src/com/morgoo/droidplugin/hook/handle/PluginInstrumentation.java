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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;

import com.morgoo.droidplugin.am.RunningActivities;
import com.morgoo.droidplugin.core.Env;
import com.morgoo.droidplugin.core.PluginProcessManager;
import com.morgoo.droidplugin.hook.HookFactory;
import com.morgoo.droidplugin.hook.binder.IWindowManagerBinderHook;
import com.morgoo.droidplugin.hook.proxy.IPackageManagerHook;
import com.morgoo.droidplugin.pm.PluginManager;
import com.morgoo.helper.Log;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2014/12/5.
 */
public class PluginInstrumentation extends Instrumentation {

    protected static final String TAG = PluginInstrumentation.class.getSimpleName();

    protected Instrumentation mTarget;
    private final Context mHostContext;
    private boolean enable = true;

    public void setEnable(boolean enable) {

        this.enable = enable;
        this.enable = true;
    }

    public PluginInstrumentation(Context hostContext, Instrumentation target) {
        mTarget = target;
        mHostContext = hostContext;
    }


    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        if (enable) {
            IWindowManagerBinderHook.fixWindowManagerHook(activity);
            IPackageManagerHook.fixContextPackageManager(activity);
            try {
                PluginProcessManager.fakeSystemService(mHostContext, activity);
            } catch (Exception e) {
                Log.e(TAG, "callActivityOnCreate:fakeSystemService", e);
            }
            try {
                onActivityCreated(activity);
            } catch (RemoteException e) {
                Log.e(TAG, "callActivityOnCreate:onActivityCreated", e);
            }
        }


        if (mTarget != null) {
            mTarget.callActivityOnCreate(activity, icicle);
        } else {
            super.callActivityOnCreate(activity, icicle);
        }
    }


    private void onActivityCreated(Activity activity) throws RemoteException {
        try {
            Intent targetIntent = activity.getIntent();
            if (targetIntent != null) {
                ActivityInfo targetInfo = targetIntent.getParcelableExtra(Env.EXTRA_TARGET_INFO);
                ActivityInfo stubInfo = targetIntent.getParcelableExtra(Env.EXTRA_STUB_INFO);
                if (targetInfo != null && stubInfo != null) {
                    RunningActivities.onActivtyCreate(activity, targetInfo, stubInfo);
                    activity.setRequestedOrientation(targetInfo.screenOrientation);
                    PluginManager.getInstance().onActivityCreated(stubInfo, targetInfo);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        fixTaskDescription(activity, targetInfo);
                    }
                }
            }
        } catch (Exception e) {
            Log.i(TAG, "onActivityCreated fail", e);
        }
    }

    private void onActivityOnNewIntent(Activity activity, Intent intent) throws RemoteException {
        //
        try {
            Intent targetIntent = activity.getIntent();
            if (targetIntent != null) {
                ActivityInfo targetInfo = targetIntent.getParcelableExtra(Env.EXTRA_TARGET_INFO);
                ActivityInfo stubInfo = targetIntent.getParcelableExtra(Env.EXTRA_STUB_INFO);
                if (targetInfo != null && stubInfo != null) {
                    RunningActivities.onActivtyOnNewIntent(activity, targetInfo, stubInfo, intent);
                    PluginManager.getInstance().onActivtyOnNewIntent(stubInfo, targetInfo, intent);
                }
            }
        } catch (Exception e) {
            Log.i(TAG, "onActivityCreated fail", e);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void fixTaskDescription(Activity activity, ActivityInfo targetInfo) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                PackageManager pm = mHostContext.getPackageManager();
                String lablel = String.valueOf(targetInfo.loadLabel(pm));
                Drawable icon = targetInfo.loadIcon(pm);
                Bitmap bitmap = null;
                if (icon instanceof BitmapDrawable) {
                    bitmap = ((BitmapDrawable) icon).getBitmap();
                }
                if (bitmap != null) {
                    activity.setTaskDescription(new android.app.ActivityManager.TaskDescription(lablel, bitmap));
                } else {
                    activity.setTaskDescription(new android.app.ActivityManager.TaskDescription(lablel));
                }
            }
        } catch (Throwable e) {
            Log.w(TAG, "fixTaskDescription fail", e);
        }
    }

    private void onActivityDestory(Activity activity) throws RemoteException {
        Intent targetIntent = activity.getIntent();
        if (targetIntent != null) {
            ActivityInfo targetInfo = targetIntent.getParcelableExtra(Env.EXTRA_TARGET_INFO);
            ActivityInfo stubInfo = targetIntent.getParcelableExtra(Env.EXTRA_STUB_INFO);
            if (targetInfo != null && stubInfo != null) {
                PluginManager.getInstance().onActivityDestory(stubInfo, targetInfo);
            }
        }
    }

    @Override
    public void callActivityOnDestroy(Activity activity) {
        if (mTarget != null) {
            mTarget.callActivityOnDestroy(activity);
        } else {
            super.callActivityOnDestroy(activity);
        }
        RunningActivities.onActivtyDestory(activity);

        if (enable) {
            try {
                onActivityDestory(activity);
            } catch (RemoteException e) {
                Log.e(TAG, "callActivityOnDestroy:onActivityDestory", e);
            }
        }
    }

    @Override
    public void callApplicationOnCreate(Application app) {

        if (enable) {
            IPackageManagerHook.fixContextPackageManager(app);
            try {
                PluginProcessManager.fakeSystemService(mHostContext, app);
            } catch (Exception e) {
                Log.e(TAG, "fakeSystemService", e);
            }
        }

        try {
            HookFactory.getInstance().onCallApplicationOnCreate(mHostContext, app);
        } catch (Exception e) {
            Log.e(TAG, "onCallApplicationOnCreate", e);
        }

        if (mTarget != null) {
            mTarget.callApplicationOnCreate(app);
        } else {
            super.callApplicationOnCreate(app);
        }

        if (enable) {
            try {
                PluginProcessManager.registerStaticReceiver(app, app.getApplicationInfo(), app.getClassLoader());
            } catch (Exception e) {
                Log.e(TAG, "registerStaticReceiver", e);
            }
        }
    }

    @Override
    public void callActivityOnNewIntent(Activity activity, Intent intent) {
//        if (activity != null && intent != null) {
//            intent.setClassName(activity.getPackageName(), activity.getClass().getName());
//        }
        try {
            Intent newIntent = intent.getParcelableExtra(Env.EXTRA_TARGET_INTENT);
            if (newIntent != null) {
                intent = newIntent;
            }
        } catch (Throwable e) {
            Log.e(TAG, "callActivityOnNewIntent:read EXTRA_TARGET_INTENT", e);
        }
        if (enable) {
            try {
                onActivityOnNewIntent(activity, intent);
            } catch (RemoteException e) {
                Log.e(TAG, "callActivityOnNewIntent:onActivityOnNewIntent", e);
            }
        }
        if (mTarget != null) {
            mTarget.callActivityOnNewIntent(activity, intent);
        } else {
            super.callActivityOnNewIntent(activity, intent);
        }
    }


}
