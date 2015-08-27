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

package com.morgoo.droidplugin.pm.parser;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;

import com.morgoo.droidplugin.reflect.MethodUtils;
import com.morgoo.helper.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/5/29.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
class PackageParserApi22 extends PackageParserApi21 {
    private static final String TAG = PackageParserApi22Preview1.class.getSimpleName();

    PackageParserApi22(Context context) throws Exception {
        super(context);
    }

    @Override
    public PackageInfo generatePackageInfo(
            int gids[], int flags, long firstInstallTime, long lastUpdateTime,
            HashSet<String> grantedPermissions) throws Exception {
        /*public static PackageInfo generatePackageInfo(PackageParser.Package p,
            int gids[], int flags, long firstInstallTime, long lastUpdateTime,
            HashSet<String> grantedPermissions, PackageUserState state, int userId) */
        try {
            return super.generatePackageInfo(gids, flags, firstInstallTime, lastUpdateTime, grantedPermissions);
        } catch (Exception e) {
            Log.i(TAG, "generatePackageInfo fail", e);
        }

        Method method = MethodUtils.getAccessibleMethod(sPackageParserClass, "generatePackageInfo",
                mPackage.getClass(),
                int[].class, int.class, long.class, long.class, sArraySetClass, sPackageUserStateClass, int.class);
        Object grantedPermissionsArray = null;
        try {
            Constructor constructor = sArraySetClass.getConstructor(Collection.class);
            grantedPermissionsArray = constructor.newInstance(constructor, grantedPermissions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (grantedPermissionsArray == null) {
            grantedPermissionsArray = grantedPermissions;
        }
        return (PackageInfo) method.invoke(null, mPackage, gids, flags, firstInstallTime, lastUpdateTime, grantedPermissionsArray, mDefaultPackageUserState, mUserId);
    }
}
