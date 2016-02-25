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

import com.morgoo.droidplugin.hook.BaseHookHandle;


/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2016/2/25.
 */
public class ILocationManagerHookHandle extends BaseHookHandle {


    public ILocationManagerHookHandle(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected void init() {
        sHookedMethodHandlers.put("requestLocationUpdates", new requestLocationUpdates(mHostContext));
        sHookedMethodHandlers.put("removeUpdates", new removeUpdates(mHostContext));
        sHookedMethodHandlers.put("requestGeofence", new requestGeofence(mHostContext));
        sHookedMethodHandlers.put("removeGeofence", new removeGeofence(mHostContext));
        sHookedMethodHandlers.put("getLastLocation", new getLastLocation(mHostContext));
        sHookedMethodHandlers.put("addGpsStatusListener", new addGpsStatusListener(mHostContext));
        sHookedMethodHandlers.put("removeGpsStatusListener", new removeGpsStatusListener(mHostContext));
        sHookedMethodHandlers.put("geocoderIsPresent", new geocoderIsPresent(mHostContext));
    }

    private static class BaseILocationManagerHookedMethodHandler extends ReplaceCallingPackageHookedMethodHandler {
        public BaseILocationManagerHookedMethodHandler(Context hostContext) {
            super(hostContext);
        }
    }

    private class requestLocationUpdates extends BaseILocationManagerHookedMethodHandler {
        public requestLocationUpdates(Context hostContext) {
            super(hostContext);
        }
    }

    private class removeUpdates extends BaseILocationManagerHookedMethodHandler {
        public removeUpdates(Context hostContext) {
            super(hostContext);
        }
    }

    private class requestGeofence extends BaseILocationManagerHookedMethodHandler {
        public requestGeofence(Context hostContext) {
            super(hostContext);
        }
    }

    private class removeGeofence extends BaseILocationManagerHookedMethodHandler {
        public removeGeofence(Context hostContext) {
            super(hostContext);
        }
    }

    private class getLastLocation extends BaseILocationManagerHookedMethodHandler {
        public getLastLocation(Context hostContext) {
            super(hostContext);
        }
    }

    private class addGpsStatusListener extends BaseILocationManagerHookedMethodHandler {
        public addGpsStatusListener(Context hostContext) {
            super(hostContext);
        }
    }

    private class removeGpsStatusListener extends BaseILocationManagerHookedMethodHandler {
        public removeGpsStatusListener(Context hostContext) {
            super(hostContext);
        }
    }

    private class geocoderIsPresent extends BaseILocationManagerHookedMethodHandler {
        public geocoderIsPresent(Context hostContext) {
            super(hostContext);
        }
    }
}
