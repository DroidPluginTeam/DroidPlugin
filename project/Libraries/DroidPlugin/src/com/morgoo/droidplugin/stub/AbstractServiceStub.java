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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.morgoo.helper.Log;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/3/13.
 */
public abstract class AbstractServiceStub extends Service {
    private static final String TAG = "AbstractServiceStub";

    private static ServcesManager mCreator = ServcesManager.getDefault();

    private boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
    }

    @Override
    public void onDestroy() {
        try {
            mCreator.onDestroy();
        } catch (Exception e) {
            handleException(e);
        }
        super.onDestroy();
        isRunning = false;
        try {
            synchronized (sLock) {
                sLock.notifyAll();
            }
        } catch (Exception e) {
        }
    }

    public static void startKillService(Context context, Intent service) {
        service.putExtra("ActionKillSelf", true);
        context.startService(service);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        try {
            if (intent != null) {
                if (intent.getBooleanExtra("ActionKillSelf", false)) {
                    startKillSelf();
                    if (!ServcesManager.getDefault().hasServiceRunning()) {
                        stopSelf(startId);
                        boolean stopService = getApplication().stopService(intent);
                        Log.i(TAG, "doGc Kill Process(pid=%s,uid=%s has exit) for %s onStart=%s intent=%s", android.os.Process.myPid(), android.os.Process.myUid(), getClass().getSimpleName(), stopService, intent);
                    } else {
                        Log.i(TAG, "doGc Kill Process(pid=%s,uid=%s has exit) for %s onStart intent=%s skip,has service running", android.os.Process.myPid(), android.os.Process.myUid(), getClass().getSimpleName(), intent);
                    }

                } else {
                    mCreator.onStart(this, intent, 0, startId);
                }
            }
        } catch (Throwable e) {
            handleException(e);
        }
        super.onStart(intent, startId);
    }

    private Object sLock = new Object();

    private void startKillSelf() {
        if (isRunning) {
            try {
                new Thread() {
                    @Override
                    public void run() {
                        synchronized (sLock) {
                            try {
                                sLock.wait();
                            } catch (Exception e) {
                            }
                        }
                        Log.i(TAG, "doGc Kill Process(pid=%s,uid=%s has exit) for %s 2", android.os.Process.myPid(), android.os.Process.myUid(), getClass().getSimpleName());
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleException(Throwable e) {
        Log.e(TAG, "handleException", e);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        try {
            if (rootIntent != null) {
                mCreator.onTaskRemoved(this, rootIntent);
            }
        } catch (Exception e) {
            handleException(e);
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        try {
            if (intent != null) {
                return mCreator.onBind(this, intent);
            }
        } catch (Exception e) {
            handleException(e);
        }
        return null;
    }

    @Override
    public void onRebind(Intent intent) {
        try {
            if (intent != null) {
                mCreator.onRebind(this, intent);
            }
        } catch (Exception e) {
            handleException(e);
        }
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        try {
            if (intent != null) {
                return mCreator.onUnbind(intent);
            }
        } catch (Exception e) {
            handleException(e);
        }
        return false;
    }
}
